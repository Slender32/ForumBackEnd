package com.slender.forumbackend.model.data.conversation

import com.slender.forumbackend.model.data.article.ArticleUserData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "会话")
data class ConversationData(
    @field:Schema(description = "会话 ID")
    val conversationId: Long,
    @field:Schema(description = "会话另一方用户信息")
    val peer: ArticleUserData,
    @field:Schema(description = "最近一条消息内容")
    val lastMessage: String,
    @field:Schema(description = "最近一条消息时间，Unix 毫秒时间戳")
    val lastMessageTime: Long,
    @field:Schema(description = "未读数量")
    val unreadCount: Int = 0,
)

@Schema(description = "会话列表")
data class ConversationListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<ConversationData>,
    @field:Schema(description = "下一页游标；没有后续数据时为 null")
    val nextCursor: ConversationCursorData?,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
    @field:Schema(description = "当前用户所有会话的未读消息总数")
    val totalUnreadCount: Int,
)

@Schema(description = "会话游标")
data class ConversationCursorData(
    @field:Schema(description = "会话 ID")
    val conversationId: Long,
    @field:Schema(description = "最近一条消息时间，Unix 毫秒时间戳")
    val lastMessageTime: Long,
)

@Schema(description = "创建会话结果")
data class ConversationCreateData(
    @field:Schema(description = "会话信息")
    val conversation: ConversationData,
    @field:Schema(description = "是否新建")
    val created: Boolean,
)
