package com.slender.forumbackend.model.entity.user.rbac

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("roles")
@Schema(description = "角色信息")
data class Role(
    @TableId
    @field:Schema(description = "角色 ID")
    val roleId: Long = 0,
    @field:Schema(description = "角色编码")
    val code: String,
    @field:Schema(description = "名称")
    val name: String,
    @field:Schema(description = "详细说明")
    val description: String,
    @field:Schema(description = "是否启用")
    val enabled: Boolean,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
){
    @TableField(exist = false)
    @field:Schema(description = "Spring Security 角色权限标识，格式 ROLE_加大写角色编码")
    val authority = "ROLE_${code.uppercase()}"
}
