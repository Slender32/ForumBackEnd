package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Article publish request")
data class ArticlePublishRequest(
    @field:Schema(description = "Article title", example = "My Kotlin Notes", maxLength = 255)
    @field:NotBlank(message = "Article title must not be blank")
    @field:Size(max = 255, message = "Article title length must not exceed 255")
    val title: String,

    @field:Schema(
        description = "Optional article summary. When omitted or blank, backend generates it from Markdown content.",
        example = "A short summary for the article.",
        maxLength = 512,
    )
    @field:Size(max = 512, message = "Article summary length must not exceed 512")
    val summary: String = "",

    @field:Schema(description = "Article content in Markdown format", example = "# Title\n\nHello **Markdown**.")
    @field:NotBlank(message = "Article content must not be blank")
    val content: String,

    @field:Schema(
        description = "Article cover image URL or object key. An empty string means no cover image.",
        example = "https://example.com/cover.png",
        maxLength = 1024,
    )
    @field:Size(max = 1024, message = "Article cover length must not exceed 1024")
    val cover: String = "",

    @field:ArraySchema(
        schema = Schema(implementation = ArticlePublishTagRequest::class),
        minItems = 0,
        maxItems = 10,
    )
    @field:Size(max = 10, message = "Article tags must not exceed 10")
    @field:Valid
    val tags: List<ArticlePublishTagRequest> = emptyList(),
)

@Schema(description = "Article publish tag request")
data class ArticlePublishTagRequest(
    @field:Schema(description = "Tag name", example = "Kotlin", maxLength = 64)
    @field:NotBlank(message = "Tag name must not be blank")
    @field:Size(max = 64, message = "Tag name length must not exceed 64")
    val name: String,

    @field:Schema(description = "32-bit ARGB color as signed int32", example = "-1", format = "int32")
    val color: Int,
)
