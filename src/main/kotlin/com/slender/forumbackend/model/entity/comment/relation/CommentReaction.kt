package com.slender.forumbackend.model.entity.comment.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("comment_reactions")
data class CommentReaction(
    val commentId: Long,
    val userId: Long,
    val emoji: String,
    val createTime: LocalDateTime,
)
