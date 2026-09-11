package com.slender.forumbackend.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ArticlePromotionRequest(
    @field:NotBlank
    @field:Size(max = 512)
    val content: String
)
