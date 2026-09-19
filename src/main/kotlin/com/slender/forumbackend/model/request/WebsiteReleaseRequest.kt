package com.slender.forumbackend.model.request

import com.slender.forumbackend.toolkit.NumericVersion
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "新增或修改客户端发布版本请求")
data class WebsiteReleaseRequest(
    @field:NotBlank
    @field:Pattern(regexp = "Windows|Android")
    @field:Schema(description = "发布平台：Windows 或 Android", example = "Windows")
    val platform: String,

    @field:NotBlank
    @field:Size(max = NumericVersion.MAX_LENGTH)
    @field:Pattern(regexp = NumericVersion.PATTERN, message = "版本必须为点分隔的非负整数")
    @field:Schema(description = "点分隔的非负整数版本号，按数字逐段比较，例如 1.10 大于 1.9", example = "1.1.0")
    val version: String,

    @field:Size(max = 255)
    @field:Schema(description = "标题", example = "正式版")
    val title: String = "",

    @field:Size(max = 4000)
    @field:Schema(description = "版本更新说明", example = "修复问题")
    val releaseNotes: String = "",

    @field:NotBlank
    @field:Pattern(regexp = "[a-fA-F0-9]{64}")
    @field:Schema(description = "安装包 SHA-256 校验值，64 位十六进制字符串", example = "8f8c49eacb4fb47e7d4bb24df1c814ef39d90d52bd5eb349af6c84d47a65d55b")
    val sha256: String,

    @field:NotBlank
    @field:Size(max = 2048)
    @field:Schema(description = "安装包下载地址", example = "https://cdn.example/app.msi")
    val downloadUrl: String,

    @field:Schema(description = "发布时间，不传时由服务端生成", example = "2026-09-07T12:00:00")
    val releaseDate: LocalDateTime? = null,

    @field:Schema(description = "是否启用", example = "true")
    val enabled: Boolean = true,
)
