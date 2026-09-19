package com.slender.forumbackend.service

import com.slender.forumbackend.configuration.BUSINESS_ZONE
import com.slender.forumbackend.exception.AnnouncementNotFoundException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.data.announcement.AnnouncementData
import com.slender.forumbackend.model.data.announcement.AnnouncementListData
import com.slender.forumbackend.repository.AnnouncementRepository
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class AnnouncementService(private val repository: AnnouncementRepository, private val clock: Clock) {
    fun list(placement: String, limit: Int, userId: Long?): AnnouncementListData {
        if (placement.isBlank() || placement.length > 64 || limit !in 1..100) throw InvalidRequestException("Invalid placement or limit")
        val now = clock.instant().atZone(BUSINESS_ZONE).toLocalDateTime()
        return AnnouncementListData(
            repository.list(placement, limit, userId, now).map {
                AnnouncementData(it.announcementId, it.title, it.summary, it.content,
                    it.publishedAt.atZone(BUSINESS_ZONE).toInstant().toEpochMilli(), it.isPinned, it.target, it.isRead)
            },
            userId?.let { repository.unreadCount(placement, it, now) },
        )
    }

    fun read(id: Long, userId: Long) {
        val now = clock.instant().atZone(BUSINESS_ZONE).toLocalDateTime()
        if (!repository.lockVisible(id, now)) throw AnnouncementNotFoundException()
        repository.read(id, userId, now)
    }
}
