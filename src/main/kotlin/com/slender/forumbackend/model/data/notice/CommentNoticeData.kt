package com.slender.forumbackend.model.data.notice

import com.slender.forumbackend.model.data.article.ArticleUserData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "评论通知")
data class CommentNoticeData(
    @field:Schema(description = "通知 ID")
    val noticeId: Long,
    @field:Schema(description = "文章 ID")
    val articleId: Long,
    @field:Schema(description = "评论 ID")
    val commentId: Long,
    @field:Schema(description = "触发通知的评论用户")
    val commenter: ArticleUserData,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "创建时间，Unix 毫秒时间戳")
    val createTime: Long,
    @field:Schema(description = "当前用户是否已读")
    @get:Schema(description = "当前用户是否已读")
    val isRead: Boolean = false,
    @field:Schema(description = "关联文章标题")
    val articleTitle: String = "",
    @field:Schema(description = "是否为回复通知")
    @get:Schema(description = "是否为回复通知")
    val isReply: Boolean = false,
    @field:Schema(description = "被回复用户名")
    val replyToUserName: String? = null,
)

@Schema(description = "评论通知列表")
data class CommentNoticeListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<CommentNoticeData>,
    @field:Schema(description = "下一页游标；没有后续数据时为 null")
    val nextCursor: CommentNoticeCursorData?,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
    @field:Schema(description = "未读数量")
    val unreadCount: Int,
)

@Schema(description = "评论通知游标")
data class CommentNoticeCursorData(
    @field:Schema(description = "通知 ID")
    val noticeId: Long,
    @field:Schema(description = "创建时间，Unix 毫秒时间戳")
    val createTime: Long,
)
