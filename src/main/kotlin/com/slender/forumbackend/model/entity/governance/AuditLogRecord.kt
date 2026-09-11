package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("audit_logs")
data class AuditLogRecord(
    @TableId
    val auditId: Long = 0, //TODO Review
    val operatorId: Long? = null,
    val permissionCode: String,
    val resourceType: String,
    val resourceId: String? = null,
    val action: String,
    val requestSummary: String? = null,
    val result: String,
    val createTime: LocalDateTime? = null,
)
