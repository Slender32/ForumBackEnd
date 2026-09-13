package com.slender.forumbackend.configuration.seed

import com.slender.forumbackend.mapper.ChatMessageMapper
import com.slender.forumbackend.mapper.ConversationMapper
import com.slender.forumbackend.model.entity.conversation.ChatMessage
import com.slender.forumbackend.model.entity.conversation.Conversation
import com.slender.forumbackend.model.entity.conversation.MessageStatus
import java.time.LocalDateTime
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("dev")
@Component
class MessageDataSeeder(
    private val conversationMapper: ConversationMapper,
    private val chatMessageMapper: ChatMessageMapper,
) {
    fun seed(now: LocalDateTime, users: SeedUserIds) {
        seedConversation(
            firstUserId = users.admin,
            secondUserId = users.bob,
            now = now,
            messages =
                listOf(
                    MessageSeed(
                        users.bob,
                        "Welcome to the demo forum.",
                        now.minusHours(1),
                        "demo-admin-bob-1",
                    ),
                    MessageSeed(
                        users.admin,
                        "The article feed is ready for review.",
                        now.minusMinutes(50),
                        "demo-admin-bob-2",
                    ),
                    MessageSeed(
                        users.bob,
                        "I will check the state flow article next.",
                        now.minusMinutes(40),
                        "demo-admin-bob-3",
                    ),
                ),
        )
        seedConversation(
            firstUserId = users.admin,
            secondUserId = users.carol,
            now = now,
            messages =
                listOf(
                    MessageSeed(
                        users.admin,
                        "The carousel points to the seeded articles.",
                        now.minusHours(2),
                        "demo-admin-carol-1",
                    ),
                    MessageSeed(
                        users.carol,
                        "The first screen looks consistent.",
                        now.minusMinutes(105),
                        "demo-admin-carol-2",
                    ),
                ),
        )
    }

    private fun seedConversation(
        firstUserId: Long,
        secondUserId: Long,
        now: LocalDateTime,
        messages: List<MessageSeed>,
    ) {
        val (userId1, userId2) =
            if (firstUserId < secondUserId) {
                firstUserId to secondUserId
            } else {
                secondUserId to firstUserId
            }
        val conversation =
            Conversation(
                userId1 = userId1,
                userId2 = userId2,
                createTime = messages.first().createTime,
                updateTime = messages.last().createTime,
            )
        conversationMapper.insert(conversation)
        val conversationId = conversation.conversationId.requireGeneratedId("conversation")

        var lastMessageId = 0L
        messages.forEach { seed ->
            val message =
                ChatMessage(
                    conversationId = conversationId,
                    senderId = seed.senderId,
                    content = seed.content,
                    status = MessageStatus.Sent,
                    clientMessageId = seed.clientMessageId,
                    createTime = seed.createTime,
                )
            chatMessageMapper.insert(message)
            lastMessageId = message.messageId.requireGeneratedId("message")
        }
        conversationMapper.updateById(
            conversation.copy(
                lastMessageId = lastMessageId,
                lastMessageTime = messages.last().createTime,
                updateTime = now,
            )
        )
    }

    private fun Long.requireGeneratedId(type: String): Long {
        require(this > 0) { "MessageDataSeeder failed to get generated $type id" }
        return this
    }

    private data class MessageSeed(
        val senderId: Long,
        val content: String,
        val createTime: LocalDateTime,
        val clientMessageId: String,
    )
}
