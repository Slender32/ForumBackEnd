package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "审核任务处理请求")
data class ContentReviewDecisionRequest(
    @field:Schema(description = "审核决定：true 批准，false 拒绝", example = "true")
    val approve: Boolean,

    @field:Size(max = 1000)
    @field:Schema(description = "审核备注", example = "人工复核通过")
    val note: String? = null,
)