package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "页码分页结果")
data class PageData<T>(
    @field:Schema(description = "当前页数据列表")
    val items: List<T>,
    @field:Schema(description = "当前页码，从 1 开始")
    val page: Int,
    @field:Schema(description = "每页数量")
    val size: Int,
    @field:Schema(description = "符合查询条件的记录总数")
    val total: Long
)
