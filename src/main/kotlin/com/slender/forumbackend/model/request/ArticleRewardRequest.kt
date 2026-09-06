package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "文章打赏请求")
data class ArticleRewardRequest(
    @field:Schema(description = "打赏的萌萌点数量，1..100", example = "1")
    @field:Min(1, message = "打赏数量不能小于1")
    @field:Max(100, message = "打赏数量不能大于100")
    val amount: Int = 1,
)
