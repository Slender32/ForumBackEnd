package com.slender.forumbackend.service.comment

import com.slender.forumbackend.component.comment.CommentContentValidator
import com.slender.forumbackend.component.comment.CommentDataAssembler
import com.slender.forumbackend.component.comment.CommentDeleteValidator
import com.slender.forumbackend.component.comment.CommentFactory
import com.slender.forumbackend.constant.enumeration.notice.CommentNoticeType
import com.slender.forumbackend.model.data.comment.CommentCreateData
import com.slender.forumbackend.model.data.comment.CommentDeleteData
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.comment.CommentContentRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.repository.comment.CommentStatisticRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class CommentCommandService(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentContentRepository: CommentContentRepository,
    private val commentStatisticRepository: CommentStatisticRepository,
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val commentContentValidator: CommentContentValidator,
    private val commentDeleteValidator: CommentDeleteValidator,
    private val commentFactory: CommentFactory,
    private val commentDataAssembler: CommentDataAssembler,
    private val commentNoticeService: CommentNoticeService,
) {
    fun createComment(articleId: Long, userId: Long, content: String): CommentCreateData {
        val article = articleQueryRepository.findPublishedArticleByIdOrThrow(articleId)
        val trimmedContent = commentContentValidator.validate(content)
        val now = now()
        val comment = commentFactory.createRoot(articleId, userId, trimmedContent, now)

        commentContentRepository.create(comment)
        val commentId = comment.commentId
        commentStatisticRepository.createStatistics(commentId)
        articleStatisticRepository.incrementCommentCount(articleId, 1)

        commentNoticeService.createIfNeeded(
            recipientId = article.authorId,
            senderId = userId,
            articleId = articleId,
            commentId = commentId,
            type = CommentNoticeType.COMMENT,
            now = now,
        )

        return CommentCreateData(
            comment = commentDataAssembler.assemble(listOf(comment), userId, includeReplies = false).first(),
            totalCount = commentStatisticRepository.countTotalCommentsByArticleId(articleId),
        )
    }

    fun replyComment(commentId: Long, userId: Long, content: String): CommentCreateData {
        val parentComment = commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        val trimmedContent = commentContentValidator.validate(content)
        val now = now()
        val rootCommentId = parentComment.rootCommentId.takeIf { it != ROOT_NONE } ?: parentComment.commentId
        val reply = commentFactory.createReply(parentComment, userId, trimmedContent, now)

        commentContentRepository.create(reply)
        val replyId = reply.commentId
        commentStatisticRepository.createStatistics(replyId)
        articleStatisticRepository.incrementCommentCount(parentComment.articleId, 1)
        commentStatisticRepository.incrementReplyCount(rootCommentId, 1)

        commentNoticeService.createIfNeeded(
            recipientId = parentComment.authorId,
            senderId = userId,
            articleId = parentComment.articleId,
            commentId = replyId,
            type = CommentNoticeType.REPLY,
            now = now,
        )

        return CommentCreateData(
            comment = commentDataAssembler.assemble(listOf(reply), userId, includeReplies = false).first(),
            totalCount = commentStatisticRepository.countTotalCommentsByArticleId(parentComment.articleId),
        )
    }

    fun deleteComment(commentId: Long, userId: Long): CommentDeleteData {
        val comment = commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        commentDeleteValidator.validate(comment, userId)
        commentContentRepository.markDeleted(commentId, now())
        articleStatisticRepository.incrementCommentCount(comment.articleId, -1)
        if (comment.rootCommentId != ROOT_NONE) {
            commentStatisticRepository.incrementReplyCount(comment.rootCommentId, -1)
        }
        return CommentDeleteData(
            totalCount = commentStatisticRepository.countTotalCommentsByArticleId(comment.articleId),
        )
    }

    private companion object {
        const val ROOT_NONE = 0L
    }
}
