package com.slender.forumbackend.service.comment

import com.slender.forumbackend.component.common.ReportWriter
import com.slender.forumbackend.exception.ReportSelfException
import com.slender.forumbackend.constant.enumeration.report.ReportTargetType
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import org.springframework.stereotype.Service

@Service
class CommentReportService(
    private val commentQueryRepository: CommentQueryRepository,
    private val reportWriter: ReportWriter,
) {
    fun report(commentId: Long, reporterId: Long, request: ReportRequest) {
        val comment = commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        if (comment.authorId == reporterId) throw ReportSelfException()
        reportWriter.write(reporterId, ReportTargetType.Comment, commentId, request)
    }
}
