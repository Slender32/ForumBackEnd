package com.slender.forumbackend.model.entity.user.rbac

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("role_permissions")
data class RolePermission(
    val roleId: Long,
    val permissionId: Long,
    val createTime: LocalDateTime,
)
