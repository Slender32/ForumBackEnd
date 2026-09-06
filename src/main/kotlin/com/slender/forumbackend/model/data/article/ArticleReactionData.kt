package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章表情反应信息")
data class ArticleReactionData(
    @field:Schema(description = "表情标识", example = "👍")
    val emoji: String,

    @field:Schema(description = "数量", example = "0")
    val count: Int,

    @field:Schema(description = "用户头像列表")
    val reactors: List<String>,

    @field:Schema(description = "当前登录用户是否使用了这个表情")
    val isReact: Boolean = false,
)
