package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class AdminArticleListRequest(
    @field:Min(1)
    val page: Int = 1,

    @field:Min(1)
    @field:Max(100)
    val size: Int = 20,

    val includeDeleted: Boolean = false,
    val authorId: Long? = null,
    val status: String? = null,
)
