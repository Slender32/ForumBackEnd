package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "聊天消息增量同步请求")
data class ChatMessageSinceRequest(
    @field:Schema(description = "本地最新消息 ID。只返回大于此 ID 的消息")
    @field:Min(0)
    val afterMessageId: Long = 0,
    @field:Schema(description = "返回条数，最大 100")
    @field:Min(1)
    @field:Max(100)
    val size: Int = 50,
)
