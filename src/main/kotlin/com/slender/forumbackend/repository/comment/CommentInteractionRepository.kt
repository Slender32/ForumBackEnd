package com.slender.forumbackend.repository.comment

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.slender.forumbackend.mapper.CommentLikeMapper
import com.slender.forumbackend.mapper.CommentStatisticsMapper
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.comment.relation.CommentLike
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class CommentInteractionRepository(
    private val commentStatisticsMapper: CommentStatisticsMapper,
    private val commentLikeMapper: CommentLikeMapper,
) {
    fun findLike(commentId: Long, userId: Long): CommentLike? =
        commentLikeMapper.selectByCommentAndUser(commentId, userId)

    fun hasLike(commentId: Long, userId: Long): Boolean = findLike(commentId, userId) != null

    fun insertLike(commentId: Long, userId: Long, createTime: LocalDateTime) {
        commentLikeMapper.upsertLike(commentId, userId, createTime)
    }

    fun deleteLike(commentId: Long, userId: Long): Int =
        commentLikeMapper.deleteByCommentAndUser(commentId, userId)

    fun adjustLikeCount(commentId: Long, delta: Int) {
        val current = commentStatisticsMapper.selectById(commentId)
        val updated =
            (current ?: CommentStatistics(commentId, 0, 0)).copy(
                likeCount = (current?.likeCount ?: 0) + delta
            )
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

    fun markDeletedByComment(commentId: Long, now: LocalDateTime) {
        commentLikeMapper.update(
            null,
            UpdateWrapper<CommentLike>()
                .eq("comment_id", commentId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
    }

    fun markDeletedByArticle(articleId: Long, now: LocalDateTime) {
        val commentIds = "SELECT comment_id FROM comments WHERE article_id = $articleId"
        commentLikeMapper.update(
            null,
            UpdateWrapper<CommentLike>()
                .inSql("comment_id", commentIds)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
    }
}
