package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import com.slender.forumbackend.validation.CaptchaCode

@Schema(description = "邮箱改绑请求")
data class RebindEmailRequest(
    @field:Schema(description = "新邮箱")
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式错误")
    val newEmail: String,

    @field:Schema(description = "通过 auth/captcha 获取的6位验证码")
    @field:NotBlank(message = "验证码不能为空")
    @field:CaptchaCode
    val captcha: String,
)
