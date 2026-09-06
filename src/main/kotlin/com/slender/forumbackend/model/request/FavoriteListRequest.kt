package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "用户收藏列表请求")
data class FavoriteListRequest(
    @field:Min(-1)
    val cursorFavoriteId: Long = -1L,
    @field:Min(0)
    val cursorCreateTime: Long? = null,
    @field:Min(1)
    @field:Max(20)
    val size: Int = 10,
)
