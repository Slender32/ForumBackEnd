package com.slender.forumbackend.service.comment

import com.slender.forumbackend.constant.core.Redis.Key.COMMENT_LIKE_PENDING
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.repository.comment.CommentInteractionRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.component.common.InteractionPendingSyncExecutor
import com.slender.forumbackend.component.common.StatisticPendingWriter
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class CommentInteractionService(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val statisticPendingWriter: StatisticPendingWriter,
    private val syncExecutor: InteractionPendingSyncExecutor,
) {
    fun toggleLike(commentId: Long, userId: Long) {
        commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        statisticPendingWriter.toggleLike(
            COMMENT_LIKE_PENDING,
            commentId,
            userId,
            commentInteractionRepository.hasLike(commentId, userId),
        )
    }

    fun react(commentId: Long, userId: Long, emoji: String) {
        commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        val trimmed = emoji.trim()
        if (trimmed.isEmpty() || trimmed.length > EMOJI_LIMIT) {
            throw InvalidRequestException("表情不能为空或超过8个字符")
        }
        commentInteractionRepository.upsertReaction(commentId, userId, trimmed, now())
    }

    fun syncPendingLikes() =
        syncExecutor.syncPendingLikes(commentInteractionRepository, COMMENT_LIKE_PENDING)


    private companion object {
        const val EMOJI_LIMIT = 8
    }
}
