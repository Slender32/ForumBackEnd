package com.slender.forumbackend.model.data.comment

import com.slender.forumbackend.model.data.article.ArticleUserData

data class CommentData(
    val commentId: Long,
    val articleId: Long,
    val author: ArticleUserData,
    val publishTime: Long,
    val updateTime: Long,
    val content: String,
    val rootCommentId: Long = -1L,
    val parentCommentId: Long = -1L,
    val replyToUserId: Long = -1L,
    val replyToUserName: String = "",
    val likeCount: Int = 0,
    val replyCount: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<CommentData> = emptyList(),
)
