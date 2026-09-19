package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "评论回复游标分页查询条件")
data class CommentReplyListRequest(
    @field:Schema(description = "上一页游标回复 ID，首次请求传 -1")
    val cursorReplyId: Long = -1L,
    @field:Schema(description = "上一页游标发布时间戳，单位毫秒；首次请求不传")
    val cursorPublishTime: Long? = null,
    @field:Min(1) @field:Max(50)
    @field:Schema(description = "每页数量，范围 1..50，默认 10")
    val size: Int = 10,
)
