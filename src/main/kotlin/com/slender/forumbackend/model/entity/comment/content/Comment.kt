package com.slender.forumbackend.model.entity.comment.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("comments")
@Schema(description = "管理端评论记录")
data class Comment(
    @TableId
    @field:Schema(description = "评论 ID")
    val commentId: Long = 0,
    @field:Schema(description = "文章 ID")
    val articleId: Long,
    @field:Schema(description = "作者用户 ID")
    val authorId: Long,
    @field:Schema(description = "所属顶级评论 ID")
    val rootCommentId: Long,
    @field:Schema(description = "直接回复的评论 ID")
    val parentCommentId: Long,
    @field:Schema(description = "被回复用户 ID")
    val replyToUserId: Long,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "状态")
    val status: CommentStatus,
    @field:Schema(description = "发布时间，格式 yyyy-MM-ddTHH:mm:ss")
    val publishTime: LocalDateTime,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
)
