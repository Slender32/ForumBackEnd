package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章标签信息")
data class ArticleTagData(
    @field:Schema(description = "标签ID", example = "1")
    val tid: Long,

    @field:Schema(description = "标签名", example = "Kotlin")
    val name: String,

    val color: Int,
)
