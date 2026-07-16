package com.slender.forumbackend.model.entity.user.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("user_stats")
data class UserStatistics(
    @TableId
    val userId: Long,
    val fanCount: Int,
    val followCount: Int,
    val publishedArticleCount: Int,
    val likedCount: Int,
)
