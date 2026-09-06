package com.slender.forumbackend.model.data.comment

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "删除评论结果")
data class CommentDeleteData(
    val totalCount: Int,
)
