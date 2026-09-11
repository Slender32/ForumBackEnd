package com.slender.forumbackend.service

import com.aliyun.sdk.service.oss2.OSSClient
import com.aliyun.sdk.service.oss2.models.GetObjectRequest
import com.aliyun.sdk.service.oss2.models.PutObjectRequest
import com.aliyun.sdk.service.oss2.transport.BinaryData
import com.slender.forumbackend.component.common.AuditLogger
import com.slender.forumbackend.configuration.OSS.Companion.BUCKET_NAME
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.zip.GZIPOutputStream
import org.springframework.beans.factory.annotation.Value
import com.slender.forumbackend.repository.governance.ArchiveSourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import tools.jackson.databind.ObjectMapper

@Service
class OssArchiveService(
    private val sourceRepository: ArchiveSourceRepository,
    private val client: OSSClient,
    private val mapper: ObjectMapper,
    private val audit: AuditLogger,
    private val transactions: TransactionTemplate,
    @Value($$"${forum.archive.batch-size:500}") private val batchSize: Int,
) {
    private val tables =
        linkedMapOf(
            "article_promotions" to listOf("promotion_id"),
            "article_tags" to listOf("article_id", "tag_id"),
            "user_tags" to listOf("user_id", "tag_id"),
            "user_follows" to listOf("follower_id", "followee_id"),
            "article_reactions" to listOf("article_id", "user_id", "emoji"),
            "article_likes" to listOf("article_id", "user_id"),
            "comment_likes" to listOf("comment_id", "user_id"),
            "article_rewards" to listOf("reward_id"),
            "favorites" to listOf("favorite_id"),
            "comment_notices" to listOf("notice_id"),
            "reports" to listOf("report_id"),
            "comments" to listOf("comment_id"),
            "article_contents" to listOf("article_id"),
            "articles" to listOf("article_id"),
            "tags" to listOf("tid"),
            "user_roles" to listOf("user_id", "role_id"),
            "carousels" to listOf("carousel_id"),
        )

    @Synchronized
    fun run(cutoff: LocalDateTime): Long = runInternal(cutoff, failedOnly = false)

    @Synchronized
    fun retryFailed(cutoff: LocalDateTime): Long = runInternal(cutoff, failedOnly = true)

    private fun runInternal(cutoff: LocalDateTime, failedOnly: Boolean): Long {
        val batchId = UUID.randomUUID().toString()
        var completed = 0L
        var failed = 0L
        tables.forEach { (table, primaryKey) ->
            val selected = findRows(table, primaryKey, cutoff, failedOnly)
            if (selected.isEmpty()) return@forEach
            val groups =
                if (failedOnly) {
                    selected
                        .groupBy { row ->
                            row[ARCHIVE_OBJECT_KEY_COLUMN]?.toString() to
                                row[ARCHIVE_BATCH_ID_COLUMN]?.toString()
                        }
                        .values
                } else {
                    listOf(selected)
                }
            groups.forEach { rows ->
                val first = rows.first()
                val objectKey =
                    first[ARCHIVE_OBJECT_KEY_COLUMN]?.toString()
                        ?: "archives/$table/${LocalDateTime.now().format(MONTH_FORMAT)}/$batchId.jsonl.gz"
                val recordBatchId = first[ARCHIVE_BATCH_ID_COLUMN]?.toString() ?: batchId
                try {
                    val compressed =
                        compress(
                            rows.map { row ->
                                mapper.writeValueAsString(
                                    mapOf(
                                        "sourceTable" to table,
                                        "sourceKey" to sourceKey(row, primaryKey),
                                        "deletedAt" to row["deleted_at"],
                                        "schemaVersion" to SCHEMA_VERSION,
                                        "payload" to archivePayload(table, row),
                                    )
                                )
                            }
                        )
                    val digest = sha256(compressed)
                    prepareRecords(table, primaryKey, rows, objectKey, digest, recordBatchId)
                    verifyOrUpload(objectKey, compressed, digest)
                    completeDatabaseBatch(table, primaryKey, rows)
                    completed += rows.size
                } catch (error: Exception) {
                    failed += rows.size
                    markFailed(table, primaryKey, rows, error)
                }
            }
        }
        audit.log(
            null,
            "archive:read",
            "archive",
            batchId,
            if (failedOnly) "RETRY" else "RUN",
            "completed=$completed,failed=$failed",
            if (failed == 0L) "SUCCESS" else "FAILED",
        )
        return completed
    }

    private fun findRows(
        table: String,
        primaryKey: List<String>,
        cutoff: LocalDateTime,
        failedOnly: Boolean,
    ): List<Map<String, Any?>> {
        if (failedOnly) {
            return sourceRepository.find(table, primaryKey, cutoff, true, batchSize.coerceIn(1, MAX_BATCH_SIZE))
        }
        return sourceRepository.find(table, primaryKey, cutoff, false, batchSize.coerceIn(1, MAX_BATCH_SIZE))
    }

    private fun prepareRecords(
        table: String,
        primaryKey: List<String>,
        rows: List<Map<String, Any?>>,
        objectKey: String,
        digest: String,
        batchId: String,
    ) {
        rows.forEach { row ->
            sourceRepository.upsert(table, sourceKey(row, primaryKey), objectKey, digest, batchId, asLocalDateTime(row["deleted_at"]))
        }
    }

    private fun verifyOrUpload(objectKey: String, bytes: ByteArray, expectedDigest: String) {
        val existing = download(objectKey)
        if (existing == null || sha256(existing) != expectedDigest) {
            client.putObject(
                PutObjectRequest.newBuilder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .body(BinaryData.fromBytes(bytes))
                    .build()
            )
        }
        val verified = download(objectKey) ?: error("OSS archive object is missing after upload")
        require(sha256(verified) == expectedDigest) { "OSS archive SHA-256 verification failed" }
    }

    private fun download(objectKey: String): ByteArray? =
        runCatching {
                client
                    .getObject(
                        GetObjectRequest.newBuilder().bucket(BUCKET_NAME).key(objectKey).build()
                    )
                    .use { response -> response.body().readAllBytes() }
            }
            .getOrNull()

    private fun completeDatabaseBatch(
        table: String,
        primaryKey: List<String>,
        rows: List<Map<String, Any?>>,
    ) {
        transactions.executeWithoutResult {
            rows.forEach { row ->
                val updated =
                    sourceRepository.markCopied(table, sourceKey(row, primaryKey), asLocalDateTime(row["deleted_at"]))
                check(updated == 1) {
                    "Archive record changed concurrently: $table/${sourceKey(row, primaryKey)}"
                }
            }
            rows.forEach { row ->
                val args = primaryKey.map { row[it] }.toTypedArray()
                val marked = sourceRepository.markSource(table, primaryKey, args.toList())
                check(marked == 1) {
                    "Archive source changed concurrently: $table/${sourceKey(row, primaryKey)}"
                }
                when (table) {
                    "comments" ->
                        sourceRepository.deleteStats(table, row["comment_id"])
                    "articles" ->
                        sourceRepository.deleteStats(table, row["article_id"])
                }
                val deleted = sourceRepository.deleteSource(table, primaryKey, args.toList())
                check(deleted == 1) {
                    "Archive source was not deleted: $table/${sourceKey(row, primaryKey)}"
                }
            }
            rows.forEach { row ->
                val updated =
                    sourceRepository.markCompleted(table, sourceKey(row, primaryKey), asLocalDateTime(row["deleted_at"]))
                check(updated == 1) {
                    "Archive completion was not recorded: $table/${sourceKey(row, primaryKey)}"
                }
            }
        }
    }

    private fun markFailed(
        table: String,
        primaryKey: List<String>,
        rows: List<Map<String, Any?>>,
        error: Exception,
    ) {
        val message = (error.message ?: error.javaClass.simpleName).take(2000)
        rows.forEach { row ->
            sourceRepository.markFailed(table, sourceKey(row, primaryKey), asLocalDateTime(row["deleted_at"]), message)
        }
    }

    private fun sourceKey(row: Map<String, Any?>, columns: List<String>) =
        columns.joinToString("|") { row[it]?.toString() ?: "null" }

    private fun asLocalDateTime(value: Any?): LocalDateTime = when (value) {
        is LocalDateTime -> value
        is java.sql.Timestamp -> value.toLocalDateTime()
        else -> error("Archive row has invalid deleted_at value")
    }

    private fun archivePayload(table: String, row: Map<String, Any?>): Map<String, Any?> {
        val sourceRow = row.filterKeys { !it.startsWith("__archive_") }
        return when (table) {
            "comments" ->
                sourceRow +
                    ("statistics" to
                        sourceRepository.stats(table, row["comment_id"]))
            "articles" ->
                sourceRow +
                    ("statistics" to
                        sourceRepository.stats(table, row["article_id"]))
            else -> sourceRow
        }
    }

    private fun compress(lines: List<String>): ByteArray =
        ByteArrayOutputStream()
            .also { output ->
                GZIPOutputStream(output).use { gzip ->
                    gzip.write(lines.joinToString("\n").toByteArray())
                }
            }
            .toByteArray()

    private fun sha256(bytes: ByteArray) =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private companion object {
        const val ARCHIVE_OBJECT_KEY_COLUMN = "__archive_object_key"
        const val ARCHIVE_BATCH_ID_COLUMN = "__archive_batch_id"
        const val SCHEMA_VERSION = 1
        const val MAX_BATCH_SIZE = 5000
        val MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM")
    }
}
