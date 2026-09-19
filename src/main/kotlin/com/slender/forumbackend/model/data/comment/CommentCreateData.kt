package com.slender.forumbackend.model.data.comment

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "评论发表结果")
data class CommentCreateData(
    @field:Schema(description = "发表后的评论信息")
    val comment: CommentData,
    @field:Schema(description = "评论总数")
    val totalCount: Int,
)
