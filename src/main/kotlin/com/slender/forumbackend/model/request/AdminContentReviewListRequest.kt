package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

data class AdminContentReviewListRequest(
    @field:Pattern(regexp = "(?i)PENDING|APPROVED|REJECTED")
    val status: String? = null,
    @field:Min(1) val page: Int = 1,
    @field:Min(1) @field:Max(100) val size: Int = 20,
)
