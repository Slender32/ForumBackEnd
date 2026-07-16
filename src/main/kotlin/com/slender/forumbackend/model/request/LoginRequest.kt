package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "邮箱密码登录请求")
data class LoginRequest(
    @field:Schema(description = "登录邮箱", example = "user@example.com")
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式错误")
    val email: String,

    @field:Schema(description = "登录密码", example = "P@ssw0rd123", format = "password")
    @field:NotBlank(message = "密码不能为空")
    val password: String,
)
