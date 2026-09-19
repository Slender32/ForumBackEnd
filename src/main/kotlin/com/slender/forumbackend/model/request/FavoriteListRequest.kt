package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "用户收藏列表请求")
data class FavoriteListRequest(
    @field:Min(-1)
    @field:Schema(description = "上一页游标收藏 ID，首次请求传 -1")
    val cursorFavoriteId: Long = -1L,
    @field:Min(0)
    @field:Schema(description = "上一页游标创建时间戳，单位毫秒；首次请求不传")
    val cursorCreateTime: Long? = null,
    @field:Min(1)
    @field:Max(20)
    @field:Schema(description = "每页数量，范围 1..20，默认 10")
    val size: Int = 10,
)
