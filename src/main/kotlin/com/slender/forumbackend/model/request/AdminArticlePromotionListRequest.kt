package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "管理端文章推荐分页查询条件")
data class AdminArticlePromotionListRequest(
    @field:Min(1)
    @field:Schema(description = "页码，从 1 开始，默认 1")
    val page: Int = 1,

    @field:Min(1)
    @field:Max(100)
    @field:Schema(description = "每页数量，范围 1..100，默认 20")
    val size: Int = 20,

    @field:Schema(description = "是否包含已软删除记录，默认 false")
    val includeDeleted: Boolean = false,
    @field:Schema(description = "文章 ID")
    val articleId: Long? = null,
)
