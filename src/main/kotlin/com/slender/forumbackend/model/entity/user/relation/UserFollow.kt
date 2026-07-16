package com.slender.forumbackend.model.entity.user.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("user_follows")
data class UserFollow(
    val followerId: Long = 0,
    val followeeId: Long = 0,
    val createTime: LocalDateTime,
)
