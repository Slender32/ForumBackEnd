package com.slender.forumbackend.model.entity.user.content

import com.baomidou.mybatisplus.annotation.IdType.INPUT
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("user_stats")
data class UserStatistics(
    @TableId(type = INPUT)
    val userId: Long,
    val fanCount: Int = 0,
    val followCount: Int = 0,
    val publishedArticleCount: Int = 0,
    val likedCount: Int = 0,
    val moePoint: Int = 0,
    val promotedCount: Int = 0,
) {
    constructor(uid: Long) : this(userId = uid)
}
