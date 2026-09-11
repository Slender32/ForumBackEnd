package com.slender.forumbackend.service.admin

import com.slender.forumbackend.component.common.AuditLogger
import com.slender.forumbackend.exception.AdminResourceNotFoundException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.repository.governance.ArchiveRecordRepository
import com.slender.forumbackend.repository.governance.AuditLogRepository
import com.slender.forumbackend.service.OssArchiveService
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class AdminAuditArchiveService(
    private val auditRepository: AuditLogRepository,
    private val archiveRepository: ArchiveRecordRepository,
    private val archive: OssArchiveService,
    private val audit: AuditLogger,
) {
    fun audits(
        page: Int = 1,
        size: Int = 20,
        result: String? = null,
        resourceType: String? = null,
    ): AdminPageData<Map<String, Any?>> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return AdminPageData(
            auditRepository.page(safePage, safeSize, result, resourceType),
            safePage,
            safeSize,
            auditRepository.count(result, resourceType)
        )
    }

    fun archives(
        page: Int = 1,
        size: Int = 20,
        status: String? = null,
        sourceTable: String? = null,
    ): AdminPageData<Map<String, Any?>> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return AdminPageData(
            archiveRepository.page(safePage, safeSize, status, sourceTable),
            safePage,
            safeSize, archiveRepository.count(status, sourceTable)
        )
    }

    fun archive(id: Long) =
        archiveRepository.findMap(id)
            ?: throw AdminResourceNotFoundException("归档记录不存在")

    fun batch(batchId: String): List<Map<String, Any?>> =
        archiveRepository.findBatch(batchId)
            .takeIf { it.isNotEmpty() } ?: throw AdminResourceNotFoundException("归档批次不存在")

    fun retryFailed(operator: Long): Long =
        archive.retryFailed(now().minusDays(30)).also {
            audit.log(operator, "archive:read", "archive", null, "RETRY", "archived=$it")
        }

}
