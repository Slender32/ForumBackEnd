package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "搜索建议请求")
data class SearchSuggestionRequest(
    val keyword: String = "",
    @field:Min(1)
    @field:Max(20)
    val size: Int = 10,
)
