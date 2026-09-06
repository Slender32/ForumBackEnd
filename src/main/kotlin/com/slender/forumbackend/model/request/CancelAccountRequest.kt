package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import com.slender.forumbackend.validation.CaptchaCode

@Schema(description = "注销账号请求")
data class CancelAccountRequest(
    @field:Schema(description = "当前密码", format = "password")
    @field:NotBlank(message = "密码不能为空")
    val password: String,

    @field:Schema(description = "通过 auth/captcha 获取的当前邮箱验证码")
    @field:NotBlank(message = "验证码不能为空")
    @field:CaptchaCode
    val captcha: String,

    @field:Schema(description = "注销原因")
    @field:Size(max = 500, message = "注销原因不能超过500字符")
    val reason: String = "",
)
