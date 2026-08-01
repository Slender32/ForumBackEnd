package com.slender.forumbackend.model.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章列表分页游标")
data class ArticleCursorData(
    @field:Schema(description = "游标文章ID", example = "1")
    val articleId: Long,

    @field:Schema(description = "游标文章发布时间戳，单位毫秒", example = "1767225600000")
    val publishTime: Long,
)
