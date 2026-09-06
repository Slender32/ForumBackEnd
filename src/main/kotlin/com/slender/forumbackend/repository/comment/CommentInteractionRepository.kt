package com.slender.forumbackend.repository.comment

import com.slender.forumbackend.mapper.CommentLikeMapper
import com.slender.forumbackend.mapper.CommentReactionMapper
import com.slender.forumbackend.mapper.CommentStatisticsMapper
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.comment.relation.CommentLike
import com.slender.forumbackend.model.entity.comment.relation.CommentReaction
import com.slender.forumbackend.component.common.InteractionPendingSyncExecutor.LikeStatisticRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CommentInteractionRepository(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentStatisticsMapper: CommentStatisticsMapper,
    private val commentLikeMapper: CommentLikeMapper,
    private val commentReactionMapper: CommentReactionMapper,
) : LikeStatisticRepository {
    override fun targetExists(targetId: Long): Boolean =
        commentQueryRepository.targetExists(targetId)

    fun findLike(commentId: Long, userId: Long): CommentLike? =
        commentLikeMapper.selectByCommentAndUser(commentId, userId)

    override fun hasLike(targetId: Long, userId: Long): Boolean =
        findLike(targetId, userId) != null

    override fun insertLike(targetId: Long, userId: Long, createTime: LocalDateTime) {
        commentLikeMapper.insert(
            CommentLike(
                commentId = targetId,
                userId = userId,
                createTime = createTime,
            )
        )
    }

    override fun deleteLike(targetId: Long, userId: Long): Int =
        commentLikeMapper.deleteByCommentAndUser(targetId, userId)

    override fun adjustLikeCount(targetId: Long, delta: Int) {
        val current = commentStatisticsMapper.selectById(targetId)
        val updated = (current ?: CommentStatistics(targetId, 0, 0))
            .copy(likeCount = (current?.likeCount ?: 0) + delta)
        if (current == null) {
            commentStatisticsMapper.insert(updated)
        } else {
            commentStatisticsMapper.updateById(updated)
        }
    }

    fun findLikesByUserAndCommentIds(userId: Long, commentIds: Collection<Long>): Set<Long> {
        if (commentIds.isEmpty()) return emptySet()
        return commentLikeMapper.selectCommentIdsByUser(userId, commentIds).toSet()
    }

    fun findReactionsByCommentIds(commentIds: Collection<Long>): List<CommentReaction> {
        if (commentIds.isEmpty()) return emptyList()
        return commentReactionMapper.selectByCommentIds(commentIds.distinct())
    }

    fun upsertReaction(commentId: Long, userId: Long, emoji: String, createTime: LocalDateTime) {
        commentReactionMapper.upsertReaction(commentId, userId, emoji, createTime)
    }
}
