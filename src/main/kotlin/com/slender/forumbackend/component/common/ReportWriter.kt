package com.slender.forumbackend.component.common

import com.slender.forumbackend.exception.ReportReasonInvalidException
import com.slender.forumbackend.model.entity.report.Report
import com.slender.forumbackend.constant.enumeration.report.ReportTargetType
import com.slender.forumbackend.model.request.ReportReason
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.repository.ReportRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReportWriter(
    private val reportRepository: ReportRepository,
) {
    fun write(
        reporterId: Long,
        targetType: ReportTargetType,
        targetId: Long,
        request: ReportRequest,
    ) {
        val detail = request.detail.trim()
        if (request.reason == ReportReason.Other && detail.isEmpty())
            throw ReportReasonInvalidException()
        val now = LocalDateTime.now()
        reportRepository.insert(
            Report(
                reporterId = reporterId,
                targetType = targetType,
                targetId = targetId,
                reason = request.reason.name,
                detail = detail,
                createTime = now,
                updateTime = now,
            )
        )
    }
}
