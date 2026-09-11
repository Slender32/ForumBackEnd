package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.WebsiteIntroductionRequest
import com.slender.forumbackend.model.request.WebsiteReleaseRequest
import com.slender.forumbackend.service.admin.AdminWebsiteService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminWebsiteFacade(
    private val service: AdminWebsiteService
) {
    fun introductions(page: Int, size: Int, includeDeleted: Boolean) = service.introductions(page, size, includeDeleted)

    fun introduction(id: Long) = service.introduction(id)

    @Transactional
    fun addIntro(request: WebsiteIntroductionRequest, operator: Long) = service.addIntro(request, operator)

    @Transactional
    fun updateIntro(id: Long, request: WebsiteIntroductionRequest, operator: Long) = service.updateIntro(id, request, operator)

    @Transactional
    fun deleteIntro(id: Long, operator: Long) = service.deleteIntro(id, operator)

    fun releases(page: Int, size: Int, includeDeleted: Boolean) = service.releases(page, size, includeDeleted)

    fun release(id: Long) = service.release(id)

    @Transactional
    fun addRelease(request: WebsiteReleaseRequest, operator: Long) = service.addRelease(request, operator)

    @Transactional
    fun updateRelease(id: Long, request: WebsiteReleaseRequest, operator: Long) = service.updateRelease(id, request, operator)

    @Transactional
    fun deleteRelease(id: Long, operator: Long) = service.deleteRelease(id, operator)
}
