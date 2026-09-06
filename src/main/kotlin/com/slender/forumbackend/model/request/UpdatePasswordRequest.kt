package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "修改密码请求")
data class UpdatePasswordRequest(
    @field:Schema(description = "当前密码", format = "password")
    @field:NotBlank(message = "旧密码不能为空")
    val oldPassword: String,

    @field:Schema(description = "新密码，6到64位", format = "password")
    @field:NotBlank(message = "新密码不能为空")
    @field:Size(min = 6, max = 64, message = "新密码长度必须在6到64位之间")
    val newPassword: String,
)
