package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "发送私信请求")
data class ChatMessageSendRequest(
    @field:Schema(description = "消息内容，1..2000 字符")
    @field:NotBlank(message = "消息内容不能为空")
    @field:Size(max = 2000, message = "消息内容不能超过2000字")
    val content: String,
    @field:Schema(description = "客户端生成的幂等键")
    @field:NotBlank(message = "clientMessageId 不能为空")
    @field:Size(max = 64, message = "clientMessageId 不能超过64字")
    val clientMessageId: String,
)
