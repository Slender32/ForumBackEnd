package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章标签信息")
data class ArticleTagData(
    @field:Schema(description = "标签ID", example = "1")
    val tid: Long,

    @field:Schema(description = "标签名", example = "Kotlin")
    val name: String,

    @field:Schema(description = "32 位 ARGB 颜色值，使用有符号整数表示")
    val color: Int,
)
