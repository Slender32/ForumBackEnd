package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "发送注册验证码请求")
data class CaptchaRequest(
    @field:Schema(description = "接收验证码的邮箱", example = "user@example.com")
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式错误")
    val email: String,
)
