package com.slender.forumbackend.service.admin

import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.entity.report.Report
import com.slender.forumbackend.model.request.AdminReportUpdateRequest
import com.slender.forumbackend.repository.ReportRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class AdminReportService(
    private val repository: ReportRepository
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean): AdminPageData<Report> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return AdminPageData(
            repository.list(safePage, safeSize, includeDeleted),
            safePage,
            safeSize,
            repository.count(includeDeleted),
        )
    }

    fun get(id: Long): Report = repository.find(id) ?: throw InvalidRequestException("举报不存在")

    fun update(id: Long, operatorId: Long, request: AdminReportUpdateRequest): Report {
        val current = get(id)
        val updated =
            current.copy(
                status = request.status,
                handlerId = operatorId,
                handledAt = now(),
                resolutionNote = request.resolutionNote?.trim(),
                updateTime = now(),
            )
        repository.update(updated)
        return updated
    }

    fun delete(id: Long) {
        repository.markDeleted(id, now())
    }
}
