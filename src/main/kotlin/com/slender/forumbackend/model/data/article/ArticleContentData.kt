package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章内容响应数据")
data class ArticleContentData(
    @field:Schema(description = "Markdown内容", example = "# Title")
    val content: String,
)
