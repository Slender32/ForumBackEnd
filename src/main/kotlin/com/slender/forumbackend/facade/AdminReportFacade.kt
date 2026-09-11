package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.AdminReportUpdateRequest
import com.slender.forumbackend.service.admin.AdminReportService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminReportFacade(
    private val service: AdminReportService
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean) = service.list(page, size, includeDeleted)

    fun get(id: Long) = service.get(id)

    @Transactional
    fun update(id: Long, operatorId: Long, request: AdminReportUpdateRequest)
        = service.update(id, operatorId, request)

    @Transactional
    fun delete(id: Long) = service.delete(id)
}
