package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Article publish response data")
data class ArticlePublishData(
    @field:Schema(description = "Published article id", example = "1")
    val articleId: Long,

    @field:Schema(description = "Published article time in milliseconds", example = "1767225600000")
    val publishTime: Long,
)
