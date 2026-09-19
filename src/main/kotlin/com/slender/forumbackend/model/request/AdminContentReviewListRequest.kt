package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

@Schema(description = "管理端审核任务分页查询条件")
data class AdminContentReviewListRequest(
    @field:Pattern(regexp = "(?i)PENDING|APPROVED|REJECTED")
    @field:Schema(description = "审核状态筛选：PENDING、APPROVED、REJECTED；不传则不限")
    val status: String? = null,
    @field:Schema(description = "页码，从 1 开始，默认 1")
    @field:Min(1) val page: Int = 1,
    @field:Schema(description = "每页数量，范围 1..100，默认 20")
    @field:Min(1) @field:Max(100) val size: Int = 20,
)
