package com.slender.forumbackend.model.entity.report

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.report.ReportStatus
import com.slender.forumbackend.constant.enumeration.report.ReportTargetType
import java.time.LocalDateTime

@TableName("reports")
data class Report(
    @TableId
    val reportId: Long = 0, //TODO Review
    val reporterId: Long,
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: String,
    val detail: String,
    val status: ReportStatus = ReportStatus.Pending,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
    val handlerId: Long? = null,
    val handledAt: LocalDateTime? = null,
    val resolutionNote: String? = null,
)
