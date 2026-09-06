package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "用户评论列表请求")
data class UserCommentListRequest(
    @field:Min(-1)
    val cursorCommentId: Long = -1L,
    @field:Min(0)
    val cursorPublishTime: Long? = null,
    @field:Min(1)
    @field:Max(50)
    val size: Int = 20,
)
