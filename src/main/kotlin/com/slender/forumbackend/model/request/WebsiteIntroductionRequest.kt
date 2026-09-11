package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class WebsiteIntroductionRequest(
    @field:NotBlank @field:Size(max = 255) @field:Schema(example = "社区介绍") val title: String,
    @field:NotBlank @field:Size(max = 2000) @field:Schema(example = "介绍正文") val description: String,
    @field:NotBlank
    @field:Size(max = 1024)
    @field:Schema(example = "https://cdn.example/intro.png")
    val imageUrl: String,
    @field:Schema(example = "10") val sortOrder: Int = 0,
    @field:Schema(example = "true") val enabled: Boolean = true,
)
