package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.governance.ArchiveRecord
import com.slender.forumbackend.model.entity.governance.AuditLogRecord
import com.slender.forumbackend.model.entity.governance.ContentReviewRecord
import java.time.LocalDateTime
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AuditLogMapper : BaseMapper<AuditLogRecord>

@Mapper
interface ArchiveRecordMapper : BaseMapper<ArchiveRecord>

@Mapper
interface ContentReviewMapper : BaseMapper<ContentReviewRecord> {
    fun selectForUpdate(@Param("id") id: Long): ContentReviewRecord?
}

@Mapper
interface ArchiveSourceMapper : BaseMapper<ArchiveRecord> {
    fun count(
        @Param("table") table: String,
        @Param("cutoff") cutoff: LocalDateTime
    ): Long

    fun find(
        @Param("table") table: String,
        @Param("keys") keys: List<String>,
        @Param("cutoff") cutoff: LocalDateTime,
        @Param("failedOnly") failedOnly: Boolean,
        @Param("limit") limit: Int,
    ): List<Map<String, Any?>>

    fun upsert(
        @Param("table") table: String,
        @Param("sourceId") sourceId: String,
        @Param("objectKey") objectKey: String,
        @Param("digest") digest: String,
        @Param("batchId") batchId: String,
        @Param("deletedAt") deletedAt: LocalDateTime,
    )

    fun markCopied(
        @Param("table") table: String,
        @Param("sourceId") sourceId: String,
        @Param("deletedAt") deletedAt: LocalDateTime,
    ): Int

    fun markSource(
        @Param("table") table: String,
        @Param("keys") keys: List<String>,
        @Param("values") values: List<Any?>,
    ): Int

    fun deleteSource(
        @Param("table") table: String,
        @Param("keys") keys: List<String>,
        @Param("values") values: List<Any?>,
    ): Int

    fun markCompleted(
        @Param("table") table: String,
        @Param("sourceId") sourceId: String,
        @Param("deletedAt") deletedAt: LocalDateTime,
    ): Int

    fun markFailed(
        @Param("table") table: String,
        @Param("sourceId") sourceId: String,
        @Param("deletedAt") deletedAt: LocalDateTime,
        @Param("message") message: String,
    ): Int

    fun stats(
        @Param("table") table: String,
        @Param("id") id: Any?
    ): Map<String, Any?>?

    fun deleteStats(
        @Param("table") table: String,
        @Param("id") id: Any?
    ): Int
}
