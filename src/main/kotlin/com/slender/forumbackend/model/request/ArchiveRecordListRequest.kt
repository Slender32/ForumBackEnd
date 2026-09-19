package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "归档记录分页查询条件")
data class ArchiveRecordListRequest(
    @field:Schema(description = "页码，从 1 开始，默认 1")
    @field:Min(1) val page: Int = 1,
    @field:Schema(description = "每页数量，范围 1..100，默认 20")
    @field:Min(1) @field:Max(100) val size: Int = 20,
    @field:Schema(description = "状态")
    val status: String? = null,
    @field:Schema(description = "按归档来源表筛选，不传则不限")
    val sourceTable: String? = null,
)
