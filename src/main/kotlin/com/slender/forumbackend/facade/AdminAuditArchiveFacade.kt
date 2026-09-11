package com.slender.forumbackend.facade

import com.slender.forumbackend.service.admin.AdminAuditArchiveService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminAuditArchiveFacade(
    private val service: AdminAuditArchiveService
) {
    fun audits(page: Int, size: Int, result: String?, resourceType: String?) =
        service.audits(page, size, result, resourceType)

    fun archives(page: Int, size: Int, status: String?, sourceTable: String?) =
        service.archives(page, size, status, sourceTable)

    fun archive(id: Long) = service.archive(id)

    fun batch(batchId: String) = service.batch(batchId)

    @Transactional
    fun retryFailed(operator: Long) = service.retryFailed(operator)
}
