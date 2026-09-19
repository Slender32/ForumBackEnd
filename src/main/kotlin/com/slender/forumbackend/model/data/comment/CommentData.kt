package com.slender.forumbackend.model.data.comment

import com.slender.forumbackend.model.data.article.ArticleUserData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "评论及回复信息")
data class CommentData(
    @field:Schema(description = "评论 ID")
    val commentId: Long,
    @field:Schema(description = "文章 ID")
    val articleId: Long,
    @field:Schema(description = "作者展示信息")
    val author: ArticleUserData,
    @field:Schema(description = "发布时间，Unix 毫秒时间戳")
    val publishTime: Long,
    @field:Schema(description = "最后更新时间，Unix 毫秒时间戳")
    val updateTime: Long,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "所属顶级评论 ID")
    val rootCommentId: Long = -1L,
    @field:Schema(description = "直接回复的评论 ID")
    val parentCommentId: Long = -1L,
    @field:Schema(description = "被回复用户 ID")
    val replyToUserId: Long = -1L,
    @field:Schema(description = "被回复用户名")
    val replyToUserName: String = "",
    @field:Schema(description = "点赞数量")
    val likeCount: Int = 0,
    @field:Schema(description = "回复数量")
    val replyCount: Int = 0,
    @field:Schema(description = "当前用户是否已点赞")
    @get:Schema(description = "当前用户是否已点赞")
    val isLiked: Boolean = false,
    @field:Schema(description = "回复预览列表")
    val replies: List<CommentData> = emptyList(),
)
