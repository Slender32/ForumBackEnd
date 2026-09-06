package com.slender.forumbackend.model.entity.conversation

import com.baomidou.mybatisplus.annotation.EnumValue
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("messages")
data class ChatMessage(
    @TableId
    val messageId: Long = 0,
    val conversationId: Long,
    val senderId: Long,
    val content: String,
    val status: MessageStatus = MessageStatus.Sent,
    val clientMessageId: String? = null,
    val createTime: LocalDateTime,
)

enum class MessageStatus(
    @EnumValue
    val value: String,
) {
    Sent("SENT"),
    Deleted("DELETED"),
}
