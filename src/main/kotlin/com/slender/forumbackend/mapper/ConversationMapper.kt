package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.conversation.ChatMessage
import com.slender.forumbackend.model.entity.conversation.Conversation
import com.slender.forumbackend.model.entity.conversation.ConversationRead
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

@Mapper
interface ConversationMapper : BaseMapper<Conversation> {
    fun selectAllByUser(
        @Param("userId") userId: Long
    ): List<Conversation>

    fun selectPageByUser(
        @Param("userId") userId: Long,
        @Param("cursorConversationId") cursorConversationId: Long,
        @Param("cursorLastMessageTime") cursorLastMessageTime: LocalDateTime?,
        @Param("limit") limit: Int,
    ): List<Conversation>
}

@Mapper
interface ChatMessageMapper : BaseMapper<ChatMessage> {
    fun selectPage(
        @Param("conversationId") conversationId: Long,
        @Param("cursorMessageId") cursorMessageId: Long,
        @Param("cursorSendTime") cursorSendTime: LocalDateTime?,
        @Param("limit") limit: Int,
        @Param("sentStatus") sentStatus: String,
    ): List<ChatMessage>
}

@Mapper
interface ConversationReadMapper : BaseMapper<ConversationRead> {
    fun upsert(
        @Param("conversationId") conversationId: Long,
        @Param("userId") userId: Long,
        @Param("lastReadMessageId") lastReadMessageId: Long,
        @Param("lastReadTime") lastReadTime: LocalDateTime,
    )
}
