package com.slender.forumbackend.model.data.conversation

import com.slender.forumbackend.model.data.article.ArticleUserData
import com.slender.forumbackend.model.entity.conversation.MessageType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "聊天消息")
data class ChatMessageData(
    @field:Schema(description = "消息 ID")
    val messageId: Long,
    @field:Schema(description = "会话 ID")
    val conversationId: Long,
    @field:Schema(description = "发送者展示信息")
    val sender: ArticleUserData,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "消息发送时间，Unix 毫秒时间戳")
    val sendTime: Long,
    @field:Schema(description = "消息类型")
    val messageType: MessageType = MessageType.TEXT,
    @field:Schema(description = "客户端生成的消息幂等键，用于重试去重和匹配发送结果")
    val clientMessageId: String = "",
)

@Schema(description = "聊天记录")
data class ChatMessageListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<ChatMessageData>,
    @field:Schema(description = "下一页游标；没有后续数据时为 null")
    val nextCursor: ChatMessageCursorData?,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
)

@Schema(description = "聊天记录游标")
data class ChatMessageCursorData(
    @field:Schema(description = "消息 ID")
    val messageId: Long,
    @field:Schema(description = "消息发送时间，Unix 毫秒时间戳")
    val sendTime: Long,
    @field:Schema(description = "消息类型")
    val messageType: MessageType = MessageType.TEXT,
    @field:Schema(description = "客户端生成的消息幂等键，用于重试去重和匹配发送结果")
    val clientMessageId: String = "",
)

@Schema(description = "发送消息结果")
data class ChatMessageSendData(
    @field:Schema(description = "消息信息")
    val message: ChatMessageData,
    @field:Schema(description = "客户端生成的消息幂等键，用于重试去重和匹配发送结果")
    val clientMessageId: String,
)
