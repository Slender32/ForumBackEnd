package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "新增或修改敏感词请求")
data class SensitiveWordRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 255)
    @field:Schema(description = "敏感词文本", example = "待审核词")
    val word: String,

    @field:Pattern(regexp = "(?i)EXACT|CONTAINS")
    @field:Schema(description = "匹配方式：EXACT 精确匹配，CONTAINS 包含匹配", example = "CONTAINS")
    val matchType: String = "CONTAINS",
    @field:Pattern(regexp = "(?i)BLOCK|REPLACE|REVIEW")
    @field:Schema(description = "处理方式：BLOCK 拒绝，REPLACE 替换，REVIEW 转人工审核", example = "REVIEW")
    val action: String = "BLOCK",

    @field:Size(max = 255)
    @field:Schema(description = "替换文本，仅 REPLACE 模式使用", example = "***")
    val replacement: String? = null,

    @field:Schema(description = "是否启用", example = "true")
    val enabled: Boolean = true,
)
