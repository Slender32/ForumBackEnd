package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "回复评论请求")
data class CommentReplyRequest(
    @field:Schema(description = "正文内容")
    val content: String,
)
