package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Article publish tag request")
data class ArticlePublishTagRequest(
    @field:Schema(description = "Tag name", example = "Kotlin", maxLength = 64)
    @field:NotBlank(message = "Tag name must not be blank")
    @field:Size(max = 64, message = "Tag name length must not exceed 64")
    val name: String,

    @field:Schema(description = "32-bit ARGB color as signed int32", example = "-1", format = "int32")
    val color: Int,
)