package com.slender.forumbackend.model.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ArticleUpdateRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @field:Size(max = 512)
    val summary: String = "",

    @field:NotBlank
    val content: String,

    @field:Size(max = 1024)
    val cover: String = "",

    @field:Valid
    @field:Size(max = 10)
    val tags: List<ArticlePublishTagRequest>? = null,
)
