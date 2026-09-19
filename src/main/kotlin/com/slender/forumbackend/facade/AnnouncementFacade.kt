package com.slender.forumbackend.facade

import com.slender.forumbackend.service.AnnouncementService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation.REPEATABLE_READ
import org.springframework.transaction.annotation.Transactional

@Service
class AnnouncementFacade(
    private val service: AnnouncementService
) {
    @Transactional(readOnly = true, isolation = REPEATABLE_READ)
    fun list(placement: String, limit: Int, userId: Long?) = service.list(placement, limit, userId)

    @Transactional
    fun read(id: Long, userId: Long) = service.read(id, userId)
}
