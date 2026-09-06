package com.slender.forumbackend.websocket.service

import com.slender.forumbackend.model.data.conversation.ConversationData
import com.slender.forumbackend.model.data.conversation.ConversationReadResult
import com.slender.forumbackend.model.data.conversation.ConversationSendResult
import com.slender.forumbackend.websocket.model.ConversationMessagePushData
import com.slender.forumbackend.websocket.model.ConversationReadPushData
import com.slender.forumbackend.websocket.model.ConversationUpdatedPushData
import com.slender.forumbackend.websocket.model.WsEnvelope
import com.slender.forumbackend.websocket.model.WsMessageType
import org.springframework.stereotype.Service
import java.lang.System.currentTimeMillis
import java.util.UUID

@Service
class ConversationRealtimePushService(
    private val sessionRegistry: MessageSocketSessionRegistry,
) {
    fun pushAfterSend(result: ConversationSendResult) {
        sessionRegistry.send(
            result.senderId,
            envelope(
                WsMessageType.CHAT_MESSAGE_SENT,
                ConversationMessagePushData(result.response.message, result.response.clientMessageId, true),
            ),
        )
        sessionRegistry.send(
            result.receiverId,
            envelope(
                WsMessageType.CHAT_MESSAGE_SENT,
                ConversationMessagePushData(result.response.message, null, false),
            ),
        )
        pushConversationUpdated(result.senderId, result.senderConversation)
        pushConversationUpdated(result.receiverId, result.receiverConversation)
    }

    fun pushAfterRead(result: ConversationReadResult) {
        pushConversationUpdated(result.readerId, result.readerConversation)
        pushConversationUpdated(result.peerId, result.peerConversation)
        sessionRegistry.send(
            result.peerId,
            envelope(
                WsMessageType.CONVERSATION_READ,
                ConversationReadPushData(
                    result.conversationId,
                    result.readerId,
                    result.lastReadMessageId,
                    result.readTime,
                ),
            ),
        )
    }

    private fun pushConversationUpdated(userId: Long, conversation: ConversationData) {
        sessionRegistry.send(
            userId,
            envelope(WsMessageType.CONVERSATION_UPDATED, ConversationUpdatedPushData(conversation)),
        )
    }

    private fun <T> envelope(type: WsMessageType, payload: T): WsEnvelope<T> =
        WsEnvelope(type, payload, UUID.randomUUID().toString(), currentTimeMillis())
}
