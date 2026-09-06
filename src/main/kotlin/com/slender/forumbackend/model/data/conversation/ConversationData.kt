package com.slender.forumbackend.model.data.conversation

import com.slender.forumbackend.model.data.article.ArticleUserData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "会话")
data class ConversationData(
    val conversationId: Long,
    val peer: ArticleUserData,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
)

@Schema(description = "会话列表")
data class ConversationListData(
    val items: List<ConversationData>,
    val nextCursor: ConversationCursorData?,
    val hasMore: Boolean,
    val totalUnreadCount: Int,
)

@Schema(description = "会话游标")
data class ConversationCursorData(
    val conversationId: Long,
    val lastMessageTime: Long,
)

@Schema(description = "创建会话结果")
data class ConversationCreateData(
    val conversation: ConversationData,
    val created: Boolean,
)
