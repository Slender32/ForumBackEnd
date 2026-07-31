package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "邮箱验证码注册请求")
data class RegisterRequest(
    @field:Schema(description = "用户名", example = "slender")
    @field:NotBlank(message = "用户名不能为空")
    @field:Size(max = 64, message = "用户名长度不能超过64位")
    val name: String,

    @field:Schema(description = "登录密码", example = "P@ssw0rd123", format = "password")
    @field:NotBlank(message = "密码不能为空")
    @field:Size(min = 6, max = 64, message = "密码长度必须在6到64位之间")
    val password: String,

    @field:Schema(description = "注册邮箱", example = "user@example.com")
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式错误")
    val email: String,

    @field:Schema(description = "6位数字验证码，首位不能为0", example = "123456")
    @field:NotBlank(message = "验证码不能为空")
    @field:Pattern(regexp = "^[1-9]\\d{5}$", message = "验证码格式错误")
    val captcha: String,
)
