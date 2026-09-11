package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

data class ContentReviewDecisionRequest(
    @field:Schema(example = "true")
    val approve: Boolean,

    @field:Size(max = 1000)
    @field:Schema(example = "人工复核通过")
    val note: String? = null,
)