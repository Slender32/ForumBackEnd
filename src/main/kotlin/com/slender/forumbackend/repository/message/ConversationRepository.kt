package com.slender.forumbackend.repository.message

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.ConversationField.USER_ID_1
import com.slender.forumbackend.constant.field.ConversationField.USER_ID_2
import com.slender.forumbackend.exception.ConversationNotFoundException
import com.slender.forumbackend.mapper.ConversationMapper
import com.slender.forumbackend.model.entity.conversation.Conversation
import java.time.LocalDateTime
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository

@Repository
class ConversationRepository(
    private val conversationMapper: ConversationMapper,
) : ServiceImpl<ConversationMapper, Conversation>(), IService<Conversation> {
    fun findByIdOrThrow(conversationId: Long): Conversation =
        conversationMapper.selectById(conversationId) ?: throw ConversationNotFoundException()

    fun findOrCreate(userId1: Long, userId2: Long, now: LocalDateTime): Pair<Conversation, Boolean> {
        findByUsers(userId1, userId2)?.let { return it to false }
        val (left, right) = ordered(userId1, userId2)
        return try {
            insert(
                Conversation(
                    userId1 = left,
                    userId2 = right,
                    createTime = now,
                    updateTime = now,
                )
            ) to true
        } catch (_: DuplicateKeyException) {
            (findByUsers(userId1, userId2) ?: error("conversation disappeared after conflict")) to false
        }
    }

    fun findPageByUser(
        userId: Long,
        cursorConversationId: Long,
        cursorLastMessageTime: LocalDateTime?,
        limit: Int,
    ): List<Conversation> = conversationMapper.selectPageByUser(
        userId = userId,
        cursorConversationId = cursorConversationId,
        cursorLastMessageTime = cursorLastMessageTime,
        limit = limit,
    )

    fun updateLastMessage(conversationId: Long, messageId: Long, sendTime: LocalDateTime) {
        val current = conversationMapper.selectById(conversationId) ?: return
        conversationMapper.updateById(
            current.copy(lastMessageId = messageId, lastMessageTime = sendTime, updateTime = sendTime)
        )
    }

    private fun findByUsers(userId1: Long, userId2: Long): Conversation? {
        val (left, right) = ordered(userId1, userId2)
        return conversationMapper.selectOne(
            QueryWrapper<Conversation>()
                .eq(USER_ID_1, left)
                .eq(USER_ID_2, right)
        )
    }

    private fun insert(conversation: Conversation): Conversation {
        conversationMapper.insert(conversation)
        return conversation
    }

    private fun ordered(a: Long, b: Long): Pair<Long, Long> =
        if (a < b) a to b else b to a
}
