package com.slender.forumbackend.repository.governance

import com.slender.forumbackend.mapper.ArchiveSourceMapper
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArchiveSourceRepository(private val mapper: ArchiveSourceMapper) {
    fun count(table: String, cutoff: LocalDateTime) = mapper.count(checkedTable(table), cutoff)

    fun find(table: String, keys: List<String>, cutoff: LocalDateTime, failedOnly: Boolean, limit: Int) =
        mapper.find(checkedTable(table), checkedKeys(keys), cutoff, failedOnly, limit)

    fun upsert(table: String, sourceId: String, objectKey: String, digest: String, batchId: String, deletedAt: LocalDateTime) =
        mapper.upsert(checkedTable(table), sourceId, objectKey, digest, batchId, deletedAt)

    fun markCopied(table: String, sourceId: String, deletedAt: LocalDateTime) =
        mapper.markCopied(checkedTable(table), sourceId, deletedAt)

    fun markSource(table: String, keys: List<String>, values: List<Any?>) =
        mapper.markSource(checkedTable(table), checkedKeys(keys), values)

    fun deleteSource(table: String, keys: List<String>, values: List<Any?>) =
        mapper.deleteSource(checkedTable(table), checkedKeys(keys), values)

    fun markCompleted(table: String, sourceId: String, deletedAt: LocalDateTime) =
        mapper.markCompleted(checkedTable(table), sourceId, deletedAt)

    fun markFailed(table: String, sourceId: String, deletedAt: LocalDateTime, message: String) =
        mapper.markFailed(checkedTable(table), sourceId, deletedAt, message)

    fun stats(table: String, id: Any?) = mapper.stats(checkedTable(table), id)

    fun deleteStats(table: String, id: Any?) = mapper.deleteStats(checkedTable(table), id)

    private fun checkedTable(table: String): String =
        table.takeIf { it in ALLOWED_TABLES } ?: error("Unsupported archive table")

    private fun checkedKeys(keys: List<String>): List<String> {
        require(keys.isNotEmpty() && keys.all(IDENTIFIER::matches)) {
            "Unsupported archive key"
        }
        return keys
    }

    private companion object {
        val IDENTIFIER = Regex("[a-z][a-z0-9_]*")
        val ALLOWED_TABLES =
            setOf(
                "article_promotions",
                "article_tags",
                "user_tags",
                "user_follows",
                "article_reactions",
                "article_likes",
                "comment_likes",
                "article_rewards",
                "favorites",
                "comment_notices",
                "reports",
                "comments",
                "article_contents",
                "articles",
                "tags",
                "user_roles",
                "carousels",
            )
    }
}
