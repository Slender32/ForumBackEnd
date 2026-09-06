package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "客户端版本检查请求")
data class VersionCheckRequest(
    @field:Schema(description = "平台：desktop 或 android")
    @field:NotBlank(message = "平台不能为空")
    @field:Pattern(regexp = "^(desktop|android)$", message = "平台必须是desktop或android")
    val platform: String,

    @field:Schema(description = "当前客户端版本", example = "0.1.0")
    @field:NotBlank(message = "版本不能为空")
    @field:Size(max = 32, message = "版本不能超过32字符")
    val version: String,

    @field:Schema(description = "当前客户端构建号")
    @field:Min(value = 0, message = "构建号不能小于0")
    val buildNumber: Long? = null,
)
