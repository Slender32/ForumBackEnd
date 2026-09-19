package com.slender.forumbackend.service

import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Normal
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.library.toLocalDateTime
import com.slender.forumbackend.model.data.article.ArticleUserData
import com.slender.forumbackend.model.data.notice.CommentNoticeCursorData
import com.slender.forumbackend.model.data.notice.CommentNoticeData
import com.slender.forumbackend.model.data.notice.CommentNoticeListData
import com.slender.forumbackend.model.request.CommentNoticeListRequest
import com.slender.forumbackend.model.request.CommentNoticeReadRequest
import com.slender.forumbackend.repository.comment.CommentNoticeRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.constant.enumeration.notice.CommentNoticeType.REPLY
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val commentNoticeRepository: CommentNoticeRepository,
    private val commentQueryRepository: CommentQueryRepository,
    private val userReadRepository: UserReadRepository,
    private val articleQueryRepository: ArticleQueryRepository,
) {

    fun listCommentNotices(userId: Long, request: CommentNoticeListRequest): CommentNoticeListData {
        val cursorTime = request.cursorCreateTime?.toLocalDateTime()
        val fetched = commentNoticeRepository.findPageByRecipient(
            recipientId = userId,
            cursorNoticeId = request.cursorNoticeId,
            cursorCreateTime = cursorTime,
            limit = request.size + 1,
        )
        val hasMore = fetched.size > request.size
        val page = fetched.take(request.size)
        val comments = commentQueryRepository.findByIds(page.map { it.commentId }).associateBy { it.commentId }
        val visible = page.filter { notice ->
            comments[notice.commentId]?.status == Normal
        }
        val userIds = visible.map { it.senderId } + visible.mapNotNull {
            comments[it.commentId]?.replyToUserId?.takeIf { id -> id > 0 }
        }
        val senders = userReadRepository.findByIds(userIds.distinct()).associateBy { it.uid }
        val articles = articleQueryRepository.findByIds(visible.map { it.articleId }).associateBy { it.articleId }
        return CommentNoticeListData(
            items = visible.map { notice ->
                val comment = comments[notice.commentId]
                val sender = senders[notice.senderId]
                CommentNoticeData(
                    noticeId = notice.noticeId,
                    articleId = notice.articleId,
                    commentId = notice.commentId,
                    commenter = sender?.toArticleUserData() ?: ArticleUserData(0, "", "", 0),
                    content = comment?.content.orEmpty(),
                    createTime = notice.createTime.timestamp,
                    isRead = notice.isRead,
                    articleTitle = articles[notice.articleId]?.title.orEmpty(),
                    isReply = notice.type == REPLY,
                    replyToUserName = if (notice.type == REPLY) senders[comment?.replyToUserId]?.name else null,
                )
            },
            nextCursor = if (hasMore) page.lastOrNull()?.let {
                CommentNoticeCursorData(it.noticeId, it.createTime.timestamp)
            } else null,
            hasMore = hasMore,
            unreadCount = commentNoticeRepository.countUnread(userId),
        )
    }

    fun markCommentNoticesRead(userId: Long, request: CommentNoticeReadRequest) {
        commentNoticeRepository.markRead(userId, request.noticeIds)
    }
}
