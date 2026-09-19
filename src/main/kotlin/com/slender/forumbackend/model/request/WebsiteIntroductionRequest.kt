package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "新增或修改官网介绍请求")
data class WebsiteIntroductionRequest(
    @field:NotBlank @field:Size(max = 255) @field:Schema(description = "标题", example = "社区介绍") val title: String,
    @field:NotBlank @field:Size(max = 2000) @field:Schema(description = "详细说明", example = "介绍正文") val description: String,
    @field:NotBlank
    @field:Size(max = 1024)
    @field:Schema(description = "图片访问地址", example = "https://cdn.example/intro.png")
    val imageUrl: String,
    @field:Schema(description = "展示排序值", example = "10") val sortOrder: Int = 0,
    @field:Schema(description = "是否启用", example = "true") val enabled: Boolean = true,
)
