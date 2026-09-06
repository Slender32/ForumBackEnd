package com.slender.forumbackend.model.entity.report

import com.baomidou.mybatisplus.annotation.EnumValue
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("reports")
data class Report(
    @TableId
    val reportId: Long = 0,
    val reporterId: Long,
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: String,
    val detail: String,
    val status: ReportStatus = ReportStatus.Pending,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
)

enum class ReportTargetType(
    @EnumValue
    val value: String,
) {
    Article("ARTICLE"),
    Comment("COMMENT"),
    User("USER"),
}

enum class ReportStatus(
    @EnumValue
    val value: String,
) {
    Pending("PENDING"),
    Resolved("RESOLVED"),
    Rejected("REJECTED"),
}
