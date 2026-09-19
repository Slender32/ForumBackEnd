package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "审计日志分页查询条件")
data class AuditLogListRequest(
    @field:Min(1)
    @field:Schema(description = "页码，从 1 开始，默认 1")
    val page: Int = 1,
    @field:Min(1)
    @field:Max(100)
    @field:Schema(description = "每页数量，范围 1..100，默认 20")
    val size: Int = 20,

    @field:Schema(description = "按审计执行结果筛选，不传则不限")
    val result: String? = null,
    @field:Schema(description = "按资源类型筛选，不传则不限")
    val resourceType: String? = null,
)
