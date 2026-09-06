package com.slender.forumbackend.model.entity.conversation

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("conversations")
data class Conversation(
    @TableId
    val conversationId: Long = 0,
    @TableField("user_id_1")
    val userId1: Long,
    @TableField("user_id_2")
    val userId2: Long,
    val lastMessageId: Long? = null,
    val lastMessageTime: LocalDateTime? = null,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
) {
    fun peerId(currentUserId: Long): Long =
        if (userId1 == currentUserId) userId2 else userId1

    fun isParticipant(userId: Long): Boolean =
        userId1 == userId || userId2 == userId
}
