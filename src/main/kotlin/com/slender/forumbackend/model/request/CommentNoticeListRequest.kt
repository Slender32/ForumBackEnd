package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "评论通知列表请求")
data class CommentNoticeListRequest(
    @field:Min(-1)
    @field:Schema(description = "上一页游标通知 ID，首次请求传 -1")
    val cursorNoticeId: Long = -1L,
    @field:Min(0)
    @field:Schema(description = "上一页游标创建时间戳，单位毫秒；首次请求不传")
    val cursorCreateTime: Long? = null,
    @field:Min(1)
    @field:Max(50)
    @field:Schema(description = "每页数量，范围 1..50，默认 20")
    val size: Int = 20,
)
