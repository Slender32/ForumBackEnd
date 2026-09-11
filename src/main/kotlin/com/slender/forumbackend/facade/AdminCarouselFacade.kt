package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.AdminCarouselRequest
import com.slender.forumbackend.service.admin.AdminCarouselService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCarouselFacade(
    private val service: AdminCarouselService
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean) =
        service.list(page, size, includeDeleted)

    @Transactional
    fun add(request: AdminCarouselRequest) = service.add(request)

    @Transactional
    fun update(id: Long, request: AdminCarouselRequest) = service.update(id, request)

    @Transactional
    fun delete(id: Long) = service.delete(id)
}
