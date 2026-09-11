package com.slender.forumbackend.service.comment

import com.slender.forumbackend.component.comment.CommentContentValidator
import com.slender.forumbackend.component.comment.CommentDataAssembler
import com.slender.forumbackend.component.comment.CommentDeleteValidator
import com.slender.forumbackend.component.comment.CommentFactory
import com.slender.forumbackend.component.common.ContentReviewWriter
import com.slender.forumbackend.component.common.ModerationStatus
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import com.slender.forumbackend.constant.enumeration.notice.CommentNoticeType
import com.slender.forumbackend.exception.CommentNotFoundException
import com.slender.forumbackend.model.data.comment.CommentCreateData
import com.slender.forumbackend.model.data.comment.CommentDeleteData
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.comment.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class CommentCommandService(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentContentRepository: CommentContentRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val commentNoticeRepository: CommentNoticeRepository,
    private val commentStatisticRepository: CommentStatisticRepository,
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val commentContentValidator: CommentContentValidator,
    private val commentDeleteValidator: CommentDeleteValidator,
    private val commentFactory: CommentFactory,
    private val commentDataAssembler: CommentDataAssembler,
    private val commentNoticeService: CommentNoticeService,
    private val reviews: ContentReviewWriter,
) {
    fun updateComment(commentId: Long, userId: Long, authorities: Set<String>, content: String) {
        val comment = findEditableComment(commentId, authorities)
        if (comment.authorId != userId && "comment:manage" !in authorities)
            throw AccessDeniedException("NO_PERMISSION")
        val moderated = commentContentValidator.validateResult(content)
        if (moderated.status == ModerationStatus.REVIEW_REQUIRED) {
            reviews.enqueue(
                "COMMENT",
                commentId,
                userId,
                mapOf("commentId" to commentId, "content" to moderated.text),
                moderated.text,
            )
            return
        }
        commentContentRepository.update(commentId, moderated.text, now())
    }

    fun createComment(articleId: Long, userId: Long, content: String): CommentCreateData {
        val article = articleQueryRepository.findPublishedArticleByIdOrThrow(articleId)
        val moderated = commentContentValidator.validateResult(content)
        val pending = moderated.status == ModerationStatus.REVIEW_REQUIRED
        val trimmedContent = moderated.text
        val comment =
            commentFactory.createRoot(
                articleId,
                userId,
                trimmedContent,
                now(),
                if (pending) CommentStatus.PendingReview else CommentStatus.Normal,
            )

        commentContentRepository.create(comment)
        val commentId = comment.commentId
        if (pending) {
            reviews.enqueue(
                "COMMENT",
                commentId,
                userId,
                mapOf(
                    "commentId" to commentId,
                    "articleId" to articleId,
                    "content" to trimmedContent,
                ),
                trimmedContent,
            )
        } else {
            commentStatisticRepository.createStatistics(commentId)
            articleStatisticRepository.incrementCommentCount(articleId, 1)

            commentNoticeService.createIfNeeded(
                recipientId = article.authorId,
                senderId = userId,
                articleId = articleId,
                commentId = commentId,
                type = CommentNoticeType.COMMENT,
                now = now(),
            )
        }

        return CommentCreateData(
            comment = commentDataAssembler
                .assemble(listOf(comment), userId, includeReplies = false)
                .first(),
            totalCount = commentStatisticRepository.countTotalCommentsByArticleId(articleId),
        )
    }

    fun replyComment(commentId: Long, userId: Long, content: String): CommentCreateData {
        val parentComment = commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        val moderated = commentContentValidator.validateResult(content)
        val pending = moderated.status == ModerationStatus.REVIEW_REQUIRED
        val trimmedContent = moderated.text
        val rootCommentId =
            parentComment.rootCommentId.takeIf { it != ROOT_NONE } ?: parentComment.commentId
        val reply =
            commentFactory.createReply(
                parentComment,
                userId,
                trimmedContent,
                now(),
                if (pending) CommentStatus.PendingReview else CommentStatus.Normal,
            )

        commentContentRepository.create(reply)
        val replyId = reply.commentId
        if (pending) {
            reviews.enqueue(
                "COMMENT",
                replyId,
                userId,
                mapOf(
                    "commentId" to replyId,
                    "articleId" to parentComment.articleId,
                    "rootCommentId" to rootCommentId,
                    "parentCommentId" to parentComment.commentId,
                    "replyToUserId" to parentComment.authorId,
                    "content" to trimmedContent,
                ),
                trimmedContent,
            )
        } else {
            commentStatisticRepository.createStatistics(replyId)
            articleStatisticRepository.incrementCommentCount(parentComment.articleId, 1)
            commentStatisticRepository.incrementReplyCount(rootCommentId, 1)

            commentNoticeService.createIfNeeded(
                recipientId = parentComment.authorId,
                senderId = userId,
                articleId = parentComment.articleId,
                commentId = replyId,
                type = CommentNoticeType.REPLY,
                now = now(),
            )
        }

        return CommentCreateData(
            comment = commentDataAssembler
                .assemble(listOf(reply), userId, includeReplies = false)
                .first(),
            totalCount =
                commentStatisticRepository.countTotalCommentsByArticleId(parentComment.articleId),
        )
    }

    fun deleteComment(
        commentId: Long,
        userId: Long,
        authorities: Set<String> = emptySet(),
    ): CommentDeleteData {
        val comment =
            if ("comment:manage" in authorities || "comment:delete:any" in authorities) {
                commentQueryRepository.findByIds(listOf(commentId)).firstOrNull()
                    ?: throw CommentNotFoundException()
            } else {
                commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
            }
        if (comment.deletedAt != null || comment.status == CommentStatus.Deleted) {
            return CommentDeleteData(
                totalCount =
                    commentStatisticRepository.countTotalCommentsByArticleId(comment.articleId)
            )
        }
        if ("comment:manage" !in authorities && "comment:delete:any" !in authorities)
            commentDeleteValidator.validate(comment, userId)

        val deletedAt = now()
        commentContentRepository.markDeleted(commentId, deletedAt)
        commentInteractionRepository.markDeletedByComment(commentId, deletedAt)
        commentNoticeRepository.markDeletedByComment(commentId, deletedAt)
        articleStatisticRepository.incrementCommentCount(comment.articleId, -1)
        if (comment.rootCommentId != ROOT_NONE) {
            commentStatisticRepository.incrementReplyCount(comment.rootCommentId, -1)
        }
        return CommentDeleteData(
            totalCount = commentStatisticRepository.countTotalCommentsByArticleId(comment.articleId)
        )
    }

    private fun findEditableComment(
        commentId: Long,
        authorities: Set<String>,
    ): com.slender.forumbackend.model.entity.comment.content.Comment {
        if ("comment:manage" in authorities || "comment:delete:any" in authorities) {
            return commentQueryRepository.findByIds(listOf(commentId)).firstOrNull()?.takeIf {
                it.deletedAt == null && it.status == CommentStatus.Normal
            } ?: throw CommentNotFoundException()
        }
        return commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
    }

    private companion object {
        const val ROOT_NONE = 0L
    }
}
