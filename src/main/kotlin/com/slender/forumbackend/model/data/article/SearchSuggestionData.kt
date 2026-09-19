package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "搜索建议")
data class SearchSuggestionData(
    @field:Schema(description = "搜索建议关键词列表")
    val suggestions: List<String>,
)
