package com.slender.forumbackend.service

import com.slender.forumbackend.exception.ConversationForbiddenException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.exception.MessageTargetInvalidException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.library.toLocalDateTime
import com.slender.forumbackend.repository.message.ChatMessageRepository
import com.slender.forumbackend.repository.message.ConversationRepository
import com.slender.forumbackend.repository.message.ConversationReadRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.model.data.article.ArticleUserData
import com.slender.forumbackend.model.data.conversation.ChatMessageCursorData
import com.slender.forumbackend.model.data.conversation.ChatMessageData
import com.slender.forumbackend.model.data.conversation.ChatMessageListData
import com.slender.forumbackend.model.data.conversation.ChatMessageSendData
import com.slender.forumbackend.model.data.conversation.ConversationCreateData
import com.slender.forumbackend.model.data.conversation.ConversationCursorData
import com.slender.forumbackend.model.data.conversation.ConversationData
import com.slender.forumbackend.model.data.conversation.ConversationListData
import com.slender.forumbackend.model.data.conversation.ConversationReadResult
import com.slender.forumbackend.model.data.conversation.ConversationSendResult
import com.slender.forumbackend.model.entity.conversation.ChatMessage
import com.slender.forumbackend.model.entity.conversation.Conversation
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.request.ChatMessageListRequest
import com.slender.forumbackend.model.request.ChatMessageSinceRequest
import com.slender.forumbackend.model.request.ChatMessageSendRequest
import com.slender.forumbackend.model.request.ConversationCreateRequest
import com.slender.forumbackend.model.request.ConversationListRequest
import com.slender.forumbackend.model.request.ConversationReadRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val conversationReadRepository: ConversationReadRepository,
    private val userReadRepository: UserReadRepository,
) {

    fun list(userId: Long, request: ConversationListRequest): ConversationListData {
        val cursorTime = request.cursorLastMessageTime?.toLocalDateTime()
        val fetched = conversationRepository.findPageByUser(
            userId = userId,
            cursorConversationId = request.cursorConversationId,
            cursorLastMessageTime = cursorTime,
            limit = request.size + 1,
        )
        val hasMore = fetched.size > request.size
        val conversations = fetched.take(request.size)
        val items = conversations.toConversationData(userId)
        val totalUnreadCount = items.sumOf { it.unreadCount }
        return ConversationListData(
            items = items,
            nextCursor = if (hasMore) conversations.lastOrNull()?.let {
                ConversationCursorData(it.conversationId, it.lastMessageTime?.timestamp ?: 0L)
            } else null,
            hasMore = hasMore,
            totalUnreadCount = totalUnreadCount,
        )
    }

    fun create(userId: Long, request: ConversationCreateRequest): ConversationCreateData {
        if (request.peerUid == userId || request.peerUid <= 0) throw InvalidRequestException("不能和自己创建会话")

        val peer = userReadRepository.findByIdOrThrow(request.peerUid)
        val now = LocalDateTime.now()
        val (conversation, created) = conversationRepository.findOrCreate(userId, request.peerUid, now)
        return ConversationCreateData(
            conversation = listOf(conversation).toConversationData(userId, mapOf(peer.uid to peer)).first(),
            created = created,
        )
    }

    fun listMessages(
        conversationId: Long,
        userId: Long,
        request: ChatMessageListRequest,
    ): ChatMessageListData {
        val conversation = requireParticipant(conversationId, userId)
        val cursorTime = request.cursorSendTime?.toLocalDateTime()
        val fetched = chatMessageRepository.findPage(
            conversationId = conversation.conversationId,
            cursorMessageId = request.cursorMessageId,
            cursorSendTime = cursorTime,
            limit = request.size + 1,
        )
        val hasMore = fetched.size > request.size
        val page = fetched.take(request.size).asReversed()
        val senders = userReadRepository.findByIds(page.map { it.senderId }).associateBy { it.uid }
        return ChatMessageListData(
            items = page.map { it.toData(senders) },
            nextCursor = if (hasMore && page.isNotEmpty()) {
                val oldest = page.first()
                ChatMessageCursorData(oldest.messageId, oldest.createTime.timestamp)
            } else null,
            hasMore = hasMore,
        )
    }

    fun listMessagesSince(
        conversationId: Long,
        userId: Long,
        request: ChatMessageSinceRequest,
    ): ChatMessageListData {
        val conversation = requireParticipant(conversationId, userId)
        val fetched = chatMessageRepository.findSince(
            conversation.conversationId,
            request.afterMessageId,
            request.size + 1,
        )
        val hasMore = fetched.size > request.size
        val page = fetched.take(request.size)
        val senders = userReadRepository.findByIds(page.map { it.senderId }).associateBy { it.uid }
        return ChatMessageListData(
            items = page.map { it.toData(senders) },
            nextCursor = page.lastOrNull()?.let { ChatMessageCursorData(it.messageId, it.createTime.timestamp) },
            hasMore = hasMore,
        )
    }

    @Transactional
    fun send(
        conversationId: Long,
        userId: Long,
        request: ChatMessageSendRequest,
    ): ConversationSendResult {
        val conversation = requireParticipant(conversationId, userId)
        val content = request.content.trim()
        val clientMessageId = request.clientMessageId.trim()
        if (content.isEmpty() || content.length > 2000 || clientMessageId.isEmpty())
            throw InvalidRequestException("消息内容或 clientMessageId 不合法")

        chatMessageRepository.findBySenderAndClientId(userId, clientMessageId)?.let { existing ->
            val sender = userReadRepository.findById(existing.senderId)
                ?: throw MessageTargetInvalidException()
            return sendResult(conversation, userId, existing, sender, clientMessageId, false)
        }
        val now = LocalDateTime.now()
        val message = chatMessageRepository.insert(
            ChatMessage(
                conversationId = conversationId,
                senderId = userId,
                content = content,
                clientMessageId = clientMessageId,
                createTime = now,
            )
        )
        conversationRepository.updateLastMessage(conversationId, message.messageId, now)
        val sender = userReadRepository.findByIdOrThrow(userId)
        return sendResult(conversation, userId, message, sender, clientMessageId, true)
    }

    @Transactional
    fun markRead(
        conversationId: Long,
        userId: Long,
        request: ConversationReadRequest,
    ): ConversationReadResult {
        val conversation = requireParticipant(conversationId, userId)
        val requestedLastReadMessageId = if (request.lastReadMessageId == -1L) {
            conversation.lastMessageId ?: 0L
        } else {
            minOf(request.lastReadMessageId, conversation.lastMessageId ?: 0L)
        }
        val currentLastReadMessageId = conversationReadRepository
            .findByUserAndConversationIds(userId, listOf(conversationId))
            .firstOrNull()
            ?.lastReadMessageId
            ?: 0L
        val lastReadMessageId = maxOf(requestedLastReadMessageId, currentLastReadMessageId)
        val readTime = LocalDateTime.now()
        conversationReadRepository.upsert(conversationId, userId, lastReadMessageId, readTime)
        val peerId = conversation.peerId(userId)
        return ConversationReadResult(
            conversationId = conversationId,
            readerId = userId,
            peerId = peerId,
            lastReadMessageId = lastReadMessageId,
            readTime = readTime.timestamp,
            readerConversation = conversationView(conversationId, userId),
            peerConversation = conversationView(conversationId, peerId),
        )
    }

    fun conversationView(conversationId: Long, currentUserId: Long): ConversationData =
        listOf(requireParticipant(conversationId, currentUserId)).toConversationData(currentUserId).first()

    private fun requireParticipant(conversationId: Long, userId: Long): Conversation {
        val conversation = conversationRepository.findByIdOrThrow(conversationId)
        if (!conversation.isParticipant(userId)) {
            throw ConversationForbiddenException()
        }
        return conversation
    }

    private fun sendResult(
        conversation: Conversation,
        senderId: Long,
        message: ChatMessage,
        sender: User,
        clientMessageId: String,
        created: Boolean,
    ): ConversationSendResult {
        val receiverId = conversation.peerId(senderId)
        return ConversationSendResult(
            response = ChatMessageSendData(message.toData(mapOf(sender.uid to sender)), clientMessageId),
            conversationId = conversation.conversationId,
            senderId = senderId,
            receiverId = receiverId,
            senderConversation = conversationView(conversation.conversationId, senderId),
            receiverConversation = conversationView(conversation.conversationId, receiverId),
            created = created,
        )
    }

    private fun List<Conversation>.toConversationData(
        currentUserId: Long,
        extraUsers: Map<Long, User> = emptyMap(),
    ): List<ConversationData> {
        if (isEmpty()) return emptyList()
        val peerIds = map { it.peerId(currentUserId) }
        val lastMessageIds = mapNotNull { it.lastMessageId }
        val users = (userReadRepository.findByIds(peerIds) + extraUsers.values).associateBy { it.uid } + extraUsers
        val lastMessages = chatMessageRepository.findByIds(lastMessageIds).associateBy { it.messageId }
        val reads = conversationReadRepository.findByUserAndConversationIds(
            currentUserId,
            map { it.conversationId },
        ).associateBy { it.conversationId }
        return map { conversation ->
            val peer = users[conversation.peerId(currentUserId)]
            val lastMessage = lastMessages[conversation.lastMessageId]
            val lastReadMessageId = reads[conversation.conversationId]?.lastReadMessageId ?: 0L
            ConversationData(
                conversationId = conversation.conversationId,
                peer = peer?.toArticleUserData()
                    ?: ArticleUserData(conversation.peerId(currentUserId), "", "", 0),
                lastMessage = lastMessage?.content ?: "",
                lastMessageTime = conversation.lastMessageTime?.timestamp
                    ?: conversation.createTime.timestamp,
                unreadCount = chatMessageRepository.countUnread(
                    conversation.conversationId,
                    currentUserId,
                    lastReadMessageId,
                ),
            )
        }
    }

    private fun ChatMessage.toData(senders: Map<Long, User>): ChatMessageData =
        ChatMessageData(
            messageId = messageId,
            conversationId = conversationId,
            sender = senders[senderId]?.toArticleUserData() ?: ArticleUserData(senderId, "", "", 0),
            content = content,
            sendTime = createTime.timestamp,
        )

}
