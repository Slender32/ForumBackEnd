package com.slender.forumbackend.repository

import com.slender.forumbackend.mapper.AnnouncementMapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class AnnouncementRepository(
    private val mapper: AnnouncementMapper
) {
    fun list(
        placement: String,
        limit: Int,
        userId: Long?,
        now: LocalDateTime
    ) = mapper.list(placement, limit, userId, now)

    fun unreadCount(
        placement: String,
        userId: Long,
        now: LocalDateTime
    ) = mapper.unreadCount(placement, userId, now)

    fun lockVisible(
        id: Long,
        now: LocalDateTime
    ) = mapper.lockVisible(id, now) != null

    fun read(
        id: Long,
        userId: Long,
        now: LocalDateTime
    ) {
        mapper.read(id, userId, now)
    }
}
