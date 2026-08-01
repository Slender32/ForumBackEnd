package com.slender.forumbackend.model.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章相关用户展示信息")
data class ArticleUserData(
    @field:Schema(description = "用户ID", example = "1")
    val uid: Long,

    @field:Schema(description = "用户名", example = "slender")
    val name: String,

    @field:Schema(description = "头像地址", example = "https://example.com/avatar.png")
    val avatar: String,

    @field:Schema(description = "用户等级，范围0到6", example = "0", minimum = "0", maximum = "6")
    val level: Int,
)
