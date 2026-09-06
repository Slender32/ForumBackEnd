package com.slender.forumbackend.websocket.model

import com.slender.forumbackend.model.data.conversation.ChatMessageData
import com.slender.forumbackend.model.data.conversation.ConversationData

data class ConversationMessagePushData(
    val message: ChatMessageData,
    val clientMessageId: String?,
    val self: Boolean,
)

data class ConversationUpdatedPushData(
    val conversation: ConversationData,
)

data class ConversationReadPushData(
    val conversationId: Long,
    val readerId: Long,
    val lastReadMessageId: Long,
    val readTime: Long,
)
