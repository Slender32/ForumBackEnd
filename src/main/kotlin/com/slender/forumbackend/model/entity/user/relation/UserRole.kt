package com.slender.forumbackend.model.entity.user.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("user_roles")
data class UserRole(
    val userId: Long,
    val roleId: Long,
    val createTime: LocalDateTime,
)
