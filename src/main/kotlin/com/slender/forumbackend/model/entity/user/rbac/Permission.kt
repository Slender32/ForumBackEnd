package com.slender.forumbackend.model.entity.user.rbac

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("permissions")
data class Permission(
    @TableId
    val permissionId: Long = 0,
    val code: String,
    val name: String,
    val resource: String,
    val action: String,
    val description: String,
    val enabled: Boolean,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
)
