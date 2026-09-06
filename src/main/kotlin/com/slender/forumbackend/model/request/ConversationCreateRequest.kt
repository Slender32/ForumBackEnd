package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "创建或获取会话请求")
data class ConversationCreateRequest(
    @field:Schema(description = "对方用户 uid")
    val peerUid: Long,
)
