package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "推荐文章请求")
data class ArticlePromotionRequest(
    @field:NotBlank
    @field:Size(max = 512)
    @field:Schema(description = "正文内容")
    val content: String
)
