package com.slender.forumbackend.facade

import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.governance.ContentReviewData
import com.slender.forumbackend.model.request.ContentReviewDecisionRequest
import com.slender.forumbackend.service.admin.AdminContentReviewService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminContentReviewFacade(
    private val service: AdminContentReviewService
) {
    fun list(status: String?, page: Int, size: Int): AdminPageData<ContentReviewData> =
        service.list(status, page, size)

    fun get(id: Long): ContentReviewData = service.get(id)

    @Transactional
    fun decide(id: Long, operator: Long, request: ContentReviewDecisionRequest): Int =
        service.decide(id, operator, request)
}
