package com.slender.forumbackend.model.entity.notice

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.notice.CommentNoticeType
import java.time.LocalDateTime

@TableName("comment_notices")
data class CommentNotice(
    @TableId
    val noticeId: Long = 0,
    val recipientId: Long,
    val commentId: Long,
    val articleId: Long,
    val senderId: Long,
    val type: CommentNoticeType,
    val isRead: Boolean = false,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
