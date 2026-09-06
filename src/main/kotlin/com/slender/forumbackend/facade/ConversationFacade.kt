package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.ChatMessageListRequest
import com.slender.forumbackend.model.request.ChatMessageSinceRequest
import com.slender.forumbackend.model.request.ChatMessageSendRequest
import com.slender.forumbackend.model.request.ConversationCreateRequest
import com.slender.forumbackend.model.request.ConversationListRequest
import com.slender.forumbackend.model.request.ConversationReadRequest
import com.slender.forumbackend.service.ConversationService
import com.slender.forumbackend.websocket.service.ConversationRealtimePushService
import org.springframework.stereotype.Service

@Service
class ConversationFacade(
    private val conversationService: ConversationService,
    private val conversationRealtimePushService: ConversationRealtimePushService,
) {
    fun list(userId: Long, request: ConversationListRequest) =
        conversationService.list(userId, request)

    fun create(userId: Long, request: ConversationCreateRequest) =
        conversationService.create(userId, request)

    fun listMessages(
        conversationId: Long,
        userId: Long,
        request: ChatMessageListRequest,
    ) = conversationService.listMessages(conversationId, userId, request)

    fun listMessagesSince(
        conversationId: Long,
        userId: Long,
        request: ChatMessageSinceRequest,
    ) = conversationService.listMessagesSince(conversationId, userId, request)

    fun send(
        conversationId: Long,
        userId: Long,
        request: ChatMessageSendRequest,
    ) = conversationService.send(conversationId, userId, request).also {
        if (it.created) conversationRealtimePushService.pushAfterSend(it)
    }.response

    fun markRead(conversationId: Long, userId: Long, request: ConversationReadRequest) {
        val result = conversationService.markRead(conversationId, userId, request)
        conversationRealtimePushService.pushAfterRead(result)
    }
}
