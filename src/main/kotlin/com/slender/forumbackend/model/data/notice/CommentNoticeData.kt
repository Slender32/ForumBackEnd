package com.slender.forumbackend.model.data.notice

import com.slender.forumbackend.model.data.article.ArticleUserData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "评论通知")
data class CommentNoticeData(
    val noticeId: Long,
    val articleId: Long,
    val commentId: Long,
    val commenter: ArticleUserData,
    val content: String,
    val createTime: Long,
    val isRead: Boolean = false,
    val articleTitle: String = "",
    val isReply: Boolean = false,
    val replyToUserName: String? = null,
)

@Schema(description = "评论通知列表")
data class CommentNoticeListData(
    val items: List<CommentNoticeData>,
    val nextCursor: CommentNoticeCursorData?,
    val hasMore: Boolean,
    val unreadCount: Int,
)

@Schema(description = "评论通知游标")
data class CommentNoticeCursorData(
    val noticeId: Long,
    val createTime: Long,
)
