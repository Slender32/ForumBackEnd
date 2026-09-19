package com.slender.forumbackend.model.data.conversation

import com.slender.forumbackend.model.data.article.ArticleUserData
import com.slender.forumbackend.model.entity.conversation.MessageType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "聊天消息")
data class ChatMessageData(
    val messageId: Long,
    val conversationId: Long,
    val sender: ArticleUserData,
    val content: String,
    val sendTime: Long,
    val messageType: MessageType = MessageType.TEXT,
    val clientMessageId: String = "",
)

@Schema(description = "聊天记录")
data class ChatMessageListData(
    val items: List<ChatMessageData>,
    val nextCursor: ChatMessageCursorData?,
    val hasMore: Boolean,
)

@Schema(description = "聊天记录游标")
data class ChatMessageCursorData(
    val messageId: Long,
    val sendTime: Long,
    val messageType: MessageType = MessageType.TEXT,
    val clientMessageId: String = "",
)

@Schema(description = "发送消息结果")
data class ChatMessageSendData(
    val message: ChatMessageData,
    val clientMessageId: String,
)
