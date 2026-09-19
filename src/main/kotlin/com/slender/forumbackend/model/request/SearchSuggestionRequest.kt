package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "搜索建议请求")
data class SearchSuggestionRequest(
    @field:Schema(description = "筛选或搜索关键词")
    val keyword: String = "",
    @field:Min(1)
    @field:Max(20)
    @field:Schema(description = "每页数量，范围 1..20，默认 10")
    val size: Int = 10,
)
