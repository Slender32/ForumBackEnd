package com.slender.forumbackend.model.data.governance

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "内容审核任务")
data class ContentReviewData(
    @field:Schema(description = "审核任务 ID")
    val reviewId: Long,
    @field:Schema(description = "审核对象类型：ARTICLE、COMMENT、USER_SIGNATURE")
    val resourceType: String,
    @field:Schema(description = "目标资源 ID")
    val resourceId: String?,
    @field:Schema(description = "内容提交者 ID")
    val authorId: Long,
    @field:Schema(description = "待审核文本")
    val content: String?,
    @field:Schema(description = "审核通过时恢复业务数据所需的快照")
    val payloadJson: String?,
    @field:Schema(description = "审核状态：PENDING、APPROVED、REJECTED")
    val status: String,
    @field:Schema(description = "执行审核的管理员 ID")
    val reviewerId: Long?,
    @field:Schema(description = "审核备注")
    val reviewNote: String?,
    val createTime: LocalDateTime?,
    val updateTime: LocalDateTime?,
)
