package com.slender.forumbackend.model.data.announcement

data class AnnouncementListData(
    val items: List<AnnouncementData>,
    val unreadCount: Long?,
)

data class AnnouncementData(
    val id: Long,
    val title: String,
    val summary: String,
    val content: String,
    val publishedAt: Long,
    val isPinned: Boolean,
    val target: String,
    val isRead: Boolean?,
)
