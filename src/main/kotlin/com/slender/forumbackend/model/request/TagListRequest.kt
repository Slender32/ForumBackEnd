package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "标签列表请求")
data class TagListRequest(
    @field:Schema(description = "筛选或搜索关键词")
    val keyword: String = "",
    @field:Min(1)
    @field:Max(50)
    @field:Schema(description = "每页数量，范围 1..50，默认 50")
    val size: Int = 50,
)
