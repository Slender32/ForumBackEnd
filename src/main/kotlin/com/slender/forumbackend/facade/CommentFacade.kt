package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.CommentListRequest
import com.slender.forumbackend.model.request.CommentReplyListRequest
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.service.comment.CommentCommandService
import com.slender.forumbackend.service.comment.CommentInteractionService
import com.slender.forumbackend.service.comment.CommentQueryService
import com.slender.forumbackend.service.comment.CommentReportService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentFacade(
    private val commentQueryService: CommentQueryService,
    private val commentCommandService: CommentCommandService,
    private val commentInteractionService: CommentInteractionService,
    private val commentReportService: CommentReportService,
) {
    @Transactional
    fun updateComment(commentId: Long, userId: Long, authorities: Set<String>, content: String) =
        commentCommandService.updateComment(commentId, userId, authorities, content)

    fun getCommentList(articleId: Long, request: CommentListRequest, currentUserId: Long?) =
        commentQueryService.getCommentList(articleId, request, currentUserId)

    @Transactional
    fun createComment(articleId: Long, userId: Long, content: String) =
        commentCommandService.createComment(articleId, userId, content)

    @Transactional
    fun replyComment(commentId: Long, userId: Long, content: String) =
        commentCommandService.replyComment(commentId, userId, content)

    fun getReplyList(commentId: Long, request: CommentReplyListRequest, currentUserId: Long?) =
        commentQueryService.getReplyList(commentId, request, currentUserId)

    fun reportComment(commentId: Long, reporterId: Long, request: ReportRequest) =
        commentReportService.report(commentId, reporterId, request)

    @Transactional
    fun deleteComment(commentId: Long, userId: Long, authorities: Set<String> = emptySet()) =
        commentCommandService.deleteComment(commentId, userId, authorities)

    @Transactional
    fun toggleLike(commentId: Long, userId: Long) =
        commentInteractionService.toggleLike(commentId, userId)
}
