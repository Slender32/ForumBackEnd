package com.slender.forumbackend.repository.message

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.ConversationField.CLIENT_MESSAGE_ID
import com.slender.forumbackend.constant.field.ConversationField.CONVERSATION_ID
import com.slender.forumbackend.constant.field.ConversationField.MESSAGE_ID
import com.slender.forumbackend.constant.field.ConversationField.SENDER_ID
import com.slender.forumbackend.constant.field.ConversationField.STATUS
import com.slender.forumbackend.mapper.ChatMessageMapper
import com.slender.forumbackend.model.entity.conversation.ChatMessage
import com.slender.forumbackend.model.entity.conversation.MessageStatus
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChatMessageRepository(
    private val chatMessageMapper: ChatMessageMapper,
) : ServiceImpl<ChatMessageMapper, ChatMessage>(), IService<ChatMessage> {
    fun insert(message: ChatMessage): ChatMessage {
        chatMessageMapper.insert(message)
        return message
    }

    fun findBySenderAndClientId(senderId: Long, clientMessageId: String): ChatMessage? =
        chatMessageMapper.selectOne(
            QueryWrapper<ChatMessage>()
                .eq(SENDER_ID, senderId)
                .eq(CLIENT_MESSAGE_ID, clientMessageId)
        )

    fun findByIds(messageIds: Collection<Long>): List<ChatMessage> =
        messageIds.distinct().takeIf { it.isNotEmpty() }?.let { chatMessageMapper.selectByIds(it) }
            ?: emptyList()

    fun findPage(
        conversationId: Long,
        cursorMessageId: Long,
        cursorSendTime: LocalDateTime?,
        limit: Int,
    ): List<ChatMessage> = chatMessageMapper.selectPage(
        conversationId = conversationId,
        cursorMessageId = cursorMessageId,
        cursorSendTime = cursorSendTime,
        limit = limit,
        sentStatus = MessageStatus.Sent.value,
    )

    fun findSince(conversationId: Long, afterMessageId: Long, limit: Int): List<ChatMessage> =
        chatMessageMapper.selectList(
            QueryWrapper<ChatMessage>()
                .eq(CONVERSATION_ID, conversationId)
                .eq(STATUS, MessageStatus.Sent.value)
                .gt(MESSAGE_ID, afterMessageId)
                .orderByAsc(MESSAGE_ID)
                .last("LIMIT $limit")
        )

    fun countUnread(conversationId: Long, userId: Long, lastReadMessageId: Long): Int =
        chatMessageMapper.selectCount(
            QueryWrapper<ChatMessage>()
                .eq(CONVERSATION_ID, conversationId)
                .eq(STATUS, MessageStatus.Sent.value)
                .ne(SENDER_ID, userId)
                .gt(MESSAGE_ID, lastReadMessageId)
        ).toInt()
}
