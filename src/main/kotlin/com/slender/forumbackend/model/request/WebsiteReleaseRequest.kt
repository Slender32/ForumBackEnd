package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class WebsiteReleaseRequest(
    @field:NotBlank
    @field:Pattern(regexp = "Windows|Android")
    @field:Schema(example = "Windows")
    val platform: String,
    @field:NotBlank @field:Size(max = 64) @field:Schema(example = "1.1.0") val version: String,
    @field:Size(max = 255) @field:Schema(example = "正式版") val title: String = "",
    @field:Size(max = 4000) @field:Schema(example = "修复问题") val releaseNotes: String = "",
    @field:NotBlank
    @field:Pattern(regexp = "[a-fA-F0-9]{64}")
    @field:Schema(example = "8f8c49eacb4fb47e7d4bb24df1c814ef39d90d52bd5eb349af6c84d47a65d55b")
    val sha256: String,
    @field:NotBlank
    @field:Size(max = 2048)
    @field:Schema(example = "https://cdn.example/app.msi")
    val downloadUrl: String,
    @field:Schema(example = "2026-09-07T12:00:00") val releaseDate: LocalDateTime? = null,
    @field:Schema(example = "true") val enabled: Boolean = true,
)