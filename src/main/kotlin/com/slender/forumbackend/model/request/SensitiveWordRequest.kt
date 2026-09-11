package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SensitiveWordRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 255)
    @field:Schema(example = "待审核词")
    val word: String,

    @field:Pattern(regexp = "(?i)EXACT|CONTAINS")
    @field:Schema(example = "CONTAINS")
    val matchType: String = "CONTAINS",
    @field:Pattern(regexp = "(?i)BLOCK|REPLACE|REVIEW")
    @field:Schema(example = "REVIEW")
    val action: String = "BLOCK",

    @field:Size(max = 255)
    @field:Schema(example = "***")
    val replacement: String? = null,

    @field:Schema(example = "true")
    val enabled: Boolean = true,
)
