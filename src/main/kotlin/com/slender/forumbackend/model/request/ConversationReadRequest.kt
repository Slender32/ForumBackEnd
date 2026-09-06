package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "标记会话已读请求")
data class ConversationReadRequest(
    @field:Schema(description = "已读到的最后一条消息 id。传 -1 表示全部标记已读")
    val lastReadMessageId: Long = -1L,
)
