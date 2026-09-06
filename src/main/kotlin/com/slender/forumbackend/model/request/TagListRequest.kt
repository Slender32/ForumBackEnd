package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "标签列表请求")
data class TagListRequest(
    val keyword: String = "",
    @field:Min(1)
    @field:Max(50)
    val size: Int = 50,
)
