package com.slender.forumbackend.model.data.comment

data class CommentReplyListData(
    val items: List<CommentData>,
    val nextCursor: CommentCursorData?,
    val hasMore: Boolean,
    val totalCount: Int,
)
