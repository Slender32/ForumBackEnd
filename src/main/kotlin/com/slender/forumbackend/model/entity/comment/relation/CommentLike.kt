package com.slender.forumbackend.model.entity.comment.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("comment_likes")
data class CommentLike(
    val commentId: Long,
    val userId: Long,
    val createTime: LocalDateTime,
)