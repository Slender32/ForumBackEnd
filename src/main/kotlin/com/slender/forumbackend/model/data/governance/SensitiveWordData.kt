package com.slender.forumbackend.model.data.governance

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "敏感词")
data class SensitiveWordData(
    @field:Schema(description = "敏感词 ID")
    val id: Long,
    @field:Schema(description = "敏感词文本")
    val word: String,
    @field:Schema(description = "匹配方式：EXACT 精确匹配，CONTAINS 包含匹配")
    val matchType: String,
    @field:Schema(description = "处理方式：BLOCK 拒绝，REPLACE 替换，REVIEW 转人工审核")
    val action: String,
    @field:Schema(description = "替换文本，仅 REPLACE 模式使用")
    val replacement: String?,
    @field:Schema(description = "是否启用")
    val enabled: Boolean,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime?,
)