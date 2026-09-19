package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive

@Schema(description = "替换用户角色请求")
data class UserRoleUpdateRequest(
    @field:Schema(description = "替换后的完整角色 ID 集合，空集合表示移除全部角色")
    val roleIds: Set<@Positive Long>
)
