package com.slender.forumbackend.model.entity.user.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("user_tags")
data class UserTag(
    val userId: Long,
    val tagId: Long,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
