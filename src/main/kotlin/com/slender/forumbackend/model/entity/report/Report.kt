package com.slender.forumbackend.model.entity.report

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.report.ReportStatus
import com.slender.forumbackend.constant.enumeration.report.ReportTargetType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("reports")
@Schema(description = "举报记录")
data class Report(
    @TableId
    @field:Schema(description = "举报 ID")
    val reportId: Long = 0,
    @field:Schema(description = "举报人用户 ID")
    val reporterId: Long,
    @field:Schema(description = "被举报对象类型")
    val targetType: ReportTargetType,
    @field:Schema(description = "被举报对象 ID")
    val targetId: Long,
    @field:Schema(description = "举报原因")
    val reason: String,
    @field:Schema(description = "举报补充说明")
    val detail: String,
    @field:Schema(description = "状态")
    val status: ReportStatus = ReportStatus.Pending,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
    @field:Schema(description = "处理人用户 ID")
    val handlerId: Long? = null,
    @field:Schema(description = "处理时间，格式 yyyy-MM-ddTHH:mm:ss")
    val handledAt: LocalDateTime? = null,
    @field:Schema(description = "举报处理备注")
    val resolutionNote: String? = null,
)
