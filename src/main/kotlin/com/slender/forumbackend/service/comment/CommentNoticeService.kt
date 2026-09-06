package com.slender.forumbackend.service.comment

import com.slender.forumbackend.constant.enumeration.notice.CommentNoticeType
import com.slender.forumbackend.model.entity.notice.CommentNotice
import com.slender.forumbackend.repository.comment.CommentNoticeRepository
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class CommentNoticeService(
    private val commentNoticeRepository: CommentNoticeRepository,
) {
    fun createIfNeeded(
        recipientId: Long,
        senderId: Long,
        articleId: Long,
        commentId: Long,
        type: CommentNoticeType,
        now: LocalDateTime,
    ) {
        if (recipientId == senderId) return
        commentNoticeRepository.insert(
            CommentNotice(
                recipientId = recipientId,
                commentId = commentId,
                articleId = articleId,
                senderId = senderId,
                type = type,
                createTime = now,
            )
        )
    }
}
