package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.CommentNoticeListRequest
import com.slender.forumbackend.model.request.CommentNoticeReadRequest
import com.slender.forumbackend.service.NoticeService
import org.springframework.stereotype.Service

@Service
class NoticeFacade(
    private val noticeService: NoticeService,
) {
    fun listCommentNotices(
        userId: Long,
        request: CommentNoticeListRequest,
    ) = noticeService.listCommentNotices(userId, request)

    fun markCommentNoticesRead(userId: Long, request: CommentNoticeReadRequest) =
        noticeService.markCommentNoticesRead(userId, request)
}
