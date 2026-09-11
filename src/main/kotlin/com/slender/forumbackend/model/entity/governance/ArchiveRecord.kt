package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("archive_records")
data class ArchiveRecord(
    @TableId
    val archiveId: Long = 0, //TODO Review
    val sourceTable: String,
    val sourceId: String,
    val objectKey: String,
    val payloadSha256: String,
    val batchId: String,
    val status: String,
    val deletedAt: LocalDateTime,
    val archivedAt: LocalDateTime? = null,
    val errorMessage: String? = null,
    val createTime: LocalDateTime? = null,
)
