package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "聊天记录请求")
data class ChatMessageListRequest(
    @field:Schema(description = "上页最早一条消息的 id。首次进入传 -1")
    @field:Min(-1)
    val cursorMessageId: Long = -1L,
    @field:Min(0)
    val cursorSendTime: Long? = null,
    @field:Min(1)
    @field:Max(50)
    val size: Int = 30,
)
