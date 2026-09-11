package com.slender.forumbackend.model.data.governance

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "敏感词")
data class SensitiveWordData(
    @field:Schema(description = "敏感词 ID")
    val id: Long,
    val word: String,
    val matchType: String,
    val action: String,
    val replacement: String?,
    val enabled: Boolean,
    val deletedAt: LocalDateTime?,
)