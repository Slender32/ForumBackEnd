package com.slender.forumbackend.model.entity.comment.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import java.time.LocalDateTime

@TableName("comments")
data class Comment(
    @TableId
    val commentId: Long = 0,
    val articleId: Long,
    val authorId: Long,
    val rootCommentId: Long,
    val parentCommentId: Long,
    val replyToUserId: Long,
    val content: String,
    val status: CommentStatus,
    val publishTime: LocalDateTime,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
