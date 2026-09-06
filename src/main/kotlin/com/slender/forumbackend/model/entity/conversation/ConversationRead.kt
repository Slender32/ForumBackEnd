package com.slender.forumbackend.model.entity.conversation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("conversation_reads")
data class ConversationRead(
    val conversationId: Long,
    val userId: Long,
    val lastReadMessageId: Long,
    val lastReadTime: LocalDateTime,
)
