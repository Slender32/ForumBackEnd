package com.slender.forumbackend.service.comment

import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.repository.comment.CommentInteractionRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.service.InteractionPendingSyncExecutor
import com.slender.forumbackend.toolkit.StatisticPendingWriter
import com.slender.forumbackend.toolkit.StatisticPendingWriter.Companion.toPendingBoolean
import com.slender.forumbackend.toolkit.StatisticPendingWriter.Companion.toPendingTarget
import com.slender.forumbackend.toolkit.deletePendingValueIfUnchanged
import com.slender.forumbackend.toolkit.pendingEntries
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class CommentInteractionService(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val redisTemplate: StringRedisTemplate,
    private val statisticPendingWriter: StatisticPendingWriter,
    private val syncExecutor: InteractionPendingSyncExecutor,
) {
    fun toggleLike(commentId: Long, userId: Long) {
        commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        statisticPendingWriter.toggleLike(commentId, userId, commentInteractionRepository)
    }

    fun react(commentId: Long, userId: Long, emoji: String) {
        commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        val trimmed = emoji.trim()
        if (trimmed.isEmpty() || trimmed.length > EMOJI_LIMIT) {
            throw InvalidRequestException("表情不能为空或超过8个字符")
        }
        commentInteractionRepository.upsertReaction(commentId, userId, trimmed, now())
    }

    fun syncPendingLikes() {
        val pendingKey = commentInteractionRepository.pendingKey
        for ((field, value) in redisTemplate.pendingEntries(pendingKey)) {
            val target = field.toPendingTarget()
            val desiredLiked = value.toPendingBoolean()
            if (target == null || desiredLiked == null || !commentInteractionRepository.targetExists(target.targetId)) {
                redisTemplate.deletePendingValueIfUnchanged(pendingKey, field, value)
                continue
            }
            syncExecutor.syncLike(commentInteractionRepository, target, field, value, desiredLiked)
        }
    }

    private companion object {
        const val EMOJI_LIMIT = 8
    }
}
