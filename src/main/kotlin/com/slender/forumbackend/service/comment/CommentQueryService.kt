package com.slender.forumbackend.service.comment

import com.slender.forumbackend.component.comment.CommentDataAssembler
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.library.toLocalDateTime
import com.slender.forumbackend.model.data.comment.CommentCursorData
import com.slender.forumbackend.model.data.comment.CommentListData
import com.slender.forumbackend.model.data.comment.CommentReplyListData
import com.slender.forumbackend.model.request.CommentListRequest
import com.slender.forumbackend.model.request.CommentReplyListRequest
import com.slender.forumbackend.model.request.CommentSort
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.repository.comment.CommentStatisticRepository
import org.springframework.stereotype.Service

@Service
class CommentQueryService(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentStatisticRepository: CommentStatisticRepository,
    private val articleQueryRepository: ArticleQueryRepository,
    private val commentDataAssembler: CommentDataAssembler,
) {
    fun getCommentList(
        articleId: Long,
        request: CommentListRequest,
        currentUserId: Long?
    ): CommentListData {
        articleQueryRepository.findPublishedArticleByIdOrThrow(articleId)

        val isHot = request.sort == CommentSort.Hot
        val comments = commentQueryRepository.findTopLevelComments(
            articleId = articleId,
            cursorCommentId = if (isHot) NO_CURSOR else request.cursorCommentId,
            cursorPublishTime = if (isHot) null else request.cursorPublishTime?.toLocalDateTime(),
            size = request.size,
            sort = request.sort.name,
        )

        val hasMore = !isHot && comments.size > request.size
        val actualComments = if (comments.size > request.size) comments.dropLast(1) else comments
        val totalCount = commentStatisticRepository.countTotalCommentsByArticleId(articleId)
        val nextCursor = if (hasMore && actualComments.isNotEmpty()) {
            val lastComment = actualComments.last()
            CommentCursorData(
                commentId = lastComment.commentId,
                publishTime = lastComment.publishTime.timestamp,
            )
        } else null

        return CommentListData(
            items = commentDataAssembler.assemble(actualComments, currentUserId, includeReplies = true),
            nextCursor = nextCursor,
            hasMore = hasMore,
            totalCount = totalCount,
        )
    }

    fun getReplyList(
        commentId: Long,
        request: CommentReplyListRequest,
        currentUserId: Long?,
    ): CommentReplyListData {
        val comment = commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        val rootId = if (comment.rootCommentId == ROOT_NONE) comment.commentId else comment.rootCommentId
        val replies = commentQueryRepository.findRepliesByRootId(
            rootCommentId = rootId,
            cursorReplyId = request.cursorReplyId,
            cursorPublishTime = request.cursorPublishTime?.toLocalDateTime(),
            size = request.size,
        )
        val hasMore = replies.size > request.size
        val actualReplies = if (hasMore) replies.dropLast(1) else replies
        val totalCount = commentStatisticRepository.countRepliesByRootId(rootId)
        val nextCursor = if (hasMore && actualReplies.isNotEmpty()) {
            val last = actualReplies.last()
            CommentCursorData(last.commentId, last.publishTime.timestamp)
        } else null

        return CommentReplyListData(
            items = commentDataAssembler.assemble(actualReplies, currentUserId, includeReplies = false),
            nextCursor = nextCursor,
            hasMore = hasMore,
            totalCount = totalCount,
        )
    }

    private companion object {
        const val ROOT_NONE = 0L
        const val NO_CURSOR = -1L
    }
}
