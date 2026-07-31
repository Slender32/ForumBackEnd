package com.slender.forumbackend.model.entity.user.content

import com.baomidou.mybatisplus.annotation.IdType.INPUT
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("user_stats")
data class UserStatistics(
    @TableId(type = INPUT)
    val userId: Long,
    val fanCount: Int,
    val followCount: Int,
    val publishedArticleCount: Int,
    val likedCount: Int,
){
    constructor(uid: Long) : this(
        uid,
        0,
        0,
        0,
        0
    )
}
