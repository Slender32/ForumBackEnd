package com.slender.forumbackend.model.entity.user.rbac

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("roles")
data class Role(
    @TableId
    val roleId: Long = 0,
    val code: String,
    val name: String,
    val description: String,
    val enabled: Boolean,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
){
    @TableField(exist = false)
    val authority = "ROLE_${code.uppercase()}"
}
