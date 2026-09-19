package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "会话列表请求")
data class ConversationListRequest(
    @field:Schema(description = "游标会话ID，首次请求传-1")
    @field:Min(-1)
    val cursorConversationId: Long = -1L,
    @field:Schema(description = "游标最后消息时间，首次请求不传")
    @field:Min(0)
    val cursorLastMessageTime: Long? = null,
    @field:Min(1)
    @field:Max(50)
    @field:Schema(description = "每页数量，范围 1..50，默认 20")
    val size: Int = 20,
)
