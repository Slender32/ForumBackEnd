package com.slender.forumbackend.model.data.user

import com.slender.forumbackend.model.data.comment.CommentCursorData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户评论列表")
data class UserCommentListData(
    val items: List<UserCommentItemData>,
    val nextCursor: CommentCursorData?,
    val hasMore: Boolean,
)

@Schema(description = "用户评论项")
data class UserCommentItemData(
    val commentId: Long,
    val content: String,
    val replyToUserName: String = "",
    val publishTime: Long,
    val likeCount: Int,
    val articleId: Long,
    val articleTitle: String,
)
