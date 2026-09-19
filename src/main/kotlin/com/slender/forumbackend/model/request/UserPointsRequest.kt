package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "积分流水分页查询条件")
data class UserPointsRequest(
    @field:Min(0)
    @field:Schema(description = "页码，从 0 开始")
    val page: Int = 0,

    @field:Min(1)
    @field:Max(100)
    @field:Schema(description = "每页数量，范围 1..100，默认 20")
    val size: Int = 20,
)
