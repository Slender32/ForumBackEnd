package com.slender.forumbackend.model.entity.comment.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("comment_stats")
data class CommentStatistics(
    @TableId
    val commentId: Long,
    val likeCount: Int,
    val replyCount: Int,
)