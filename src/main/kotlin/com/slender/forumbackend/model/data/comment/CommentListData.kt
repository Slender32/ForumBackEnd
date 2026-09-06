package com.slender.forumbackend.model.data.comment

data class CommentListData(
    val items: List<CommentData>,
    val nextCursor: CommentCursorData?,
    val hasMore: Boolean,
    val totalCount: Int,
)

data class CommentCursorData(
    val commentId: Long,
    val publishTime: Long,
)
