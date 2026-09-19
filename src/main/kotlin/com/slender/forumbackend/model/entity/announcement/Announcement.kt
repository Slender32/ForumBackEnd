package com.slender.forumbackend.model.entity.announcement

import java.time.LocalDateTime

data class Announcement(
    val announcementId: Long,
    val title: String,
    val summary: String,
    val content: String,
    val publishedAt: LocalDateTime,
    val isPinned: Boolean,
    val target: String,
    val isRead: Boolean?,
)
