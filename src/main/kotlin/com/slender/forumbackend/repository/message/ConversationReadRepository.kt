package com.slender.forumbackend.repository.message

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.ConversationField.CONVERSATION_ID
import com.slender.forumbackend.constant.field.ConversationField.USER_ID
import com.slender.forumbackend.mapper.ConversationReadMapper
import com.slender.forumbackend.model.entity.conversation.ConversationRead
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ConversationReadRepository(
    private val conversationReadMapper: ConversationReadMapper,
) : ServiceImpl<ConversationReadMapper, ConversationRead>(), IService<ConversationRead> {
    fun findByUserAndConversationIds(
        userId: Long,
        conversationIds: Collection<Long>,
    ): List<ConversationRead> {
        if (conversationIds.isEmpty()) return emptyList()
        return conversationReadMapper.selectList(
            QueryWrapper<ConversationRead>()
                .eq(USER_ID, userId)
                .`in`(CONVERSATION_ID, conversationIds.distinct())
        )
    }

    fun upsert(conversationId: Long, userId: Long, lastReadMessageId: Long, lastReadTime: LocalDateTime) {
        conversationReadMapper.upsert(conversationId, userId, lastReadMessageId, lastReadTime)
    }
}
