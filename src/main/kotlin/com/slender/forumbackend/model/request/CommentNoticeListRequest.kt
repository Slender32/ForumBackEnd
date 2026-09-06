package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "评论通知列表请求")
data class CommentNoticeListRequest(
    @field:Min(-1)
    val cursorNoticeId: Long = -1L,
    @field:Min(0)
    val cursorCreateTime: Long? = null,
    @field:Min(1)
    @field:Max(50)
    val size: Int = 20,
)
