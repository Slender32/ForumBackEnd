package com.slender.forumbackend.service.user

import com.slender.forumbackend.component.common.ReportWriter
import com.slender.forumbackend.exception.ReportSelfException
import com.slender.forumbackend.model.entity.report.ReportTargetType
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Service

@Service
class UserReportService(
    private val userReadRepository: UserReadRepository,
    private val reportWriter: ReportWriter,
) {
    fun report(targetUid: Long, reporterId: Long, request: ReportRequest) {
        userReadRepository.findByIdOrThrow(targetUid)
        if (targetUid == reporterId) throw ReportSelfException()
        reportWriter.write(reporterId, ReportTargetType.User, targetUid, request)
    }
}
