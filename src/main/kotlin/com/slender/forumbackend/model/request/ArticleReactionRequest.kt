package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "文章表情请求")
data class ArticleReactionRequest(
    @field:Schema(description = "表情", example = "👍")
    @field:NotBlank(message = "表情不能为空")
    @field:Size(max = 8, message = "表情长度不能超过8")
    val emoji: String,
)
