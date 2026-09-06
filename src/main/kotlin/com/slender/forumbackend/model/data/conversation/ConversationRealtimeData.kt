package com.slender.forumbackend.model.data.conversation

data class ConversationSendResult(
    val response: ChatMessageSendData,
    val conversationId: Long,
    val senderId: Long,
    val receiverId: Long,
    val senderConversation: ConversationData,
    val receiverConversation: ConversationData,
    val created: Boolean,
)

data class ConversationReadResult(
    val conversationId: Long,
    val readerId: Long,
    val peerId: Long,
    val lastReadMessageId: Long,
    val readTime: Long,
    val readerConversation: ConversationData,
    val peerConversation: ConversationData,
)
