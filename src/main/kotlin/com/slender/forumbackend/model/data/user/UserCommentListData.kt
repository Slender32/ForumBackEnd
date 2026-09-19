package com.slender.forumbackend.model.data.user

import com.slender.forumbackend.model.data.comment.CommentCursorData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户评论列表")
data class UserCommentListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<UserCommentItemData>,
    @field:Schema(description = "下一页游标；没有后续数据时为 null")
    val nextCursor: CommentCursorData?,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
)

@Schema(description = "用户评论项")
data class UserCommentItemData(
    @field:Schema(description = "评论 ID")
    val commentId: Long,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "被回复用户名")
    val replyToUserName: String = "",
    @field:Schema(description = "发布时间，Unix 毫秒时间戳")
    val publishTime: Long,
    @field:Schema(description = "点赞数量")
    val likeCount: Int,
    @field:Schema(description = "文章 ID")
    val articleId: Long,
    @field:Schema(description = "关联文章标题")
    val articleTitle: String,
)
