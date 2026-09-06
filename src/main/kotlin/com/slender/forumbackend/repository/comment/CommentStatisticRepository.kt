package com.slender.forumbackend.repository.comment

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Normal
import com.slender.forumbackend.mapper.CommentMapper
import com.slender.forumbackend.mapper.CommentStatisticsMapper
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import org.springframework.stereotype.Repository

@Repository
class CommentStatisticRepository(
    private val commentMapper: CommentMapper,
    private val commentStatisticsMapper: CommentStatisticsMapper,
) : ServiceImpl<CommentStatisticsMapper, CommentStatistics>(), IService<CommentStatistics> {
    fun findStatisticsByIds(commentIds: Collection<Long>): List<CommentStatistics> =
        commentIds.distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { commentStatisticsMapper.selectByIds(it) } ?: emptyList()

    fun countTotalCommentsByArticleId(articleId: Long): Int =
        commentMapper.countNormalByArticleId(articleId, Normal.value).toInt()

    fun countRepliesByRootId(rootCommentId: Long): Int =
        commentMapper.countNormalRepliesByRootId(rootCommentId, Normal.value).toInt()

    fun countRepliesByRootIds(rootCommentIds: Collection<Long>): Map<Long, Int> {
        val ids = rootCommentIds.distinct()
        if (ids.isEmpty()) return emptyMap()
        return commentMapper.countNormalRepliesByRootIds(ids, Normal.value).associate { row ->
            val rootCommentId = (row["root_comment_id"] as Number).toLong()
            val replyCount = (row["reply_count"] as Number).toInt()
            rootCommentId to replyCount
        }
    }

    fun createStatistics(commentId: Long) {
        commentStatisticsMapper.insert(
            CommentStatistics(
                commentId = commentId,
                likeCount = 0,
                replyCount = 0,
            )
        )
    }

    fun incrementReplyCount(commentId: Long, delta: Int) {
        val current = commentStatisticsMapper.selectById(commentId)
        if (current != null) {
            val updated = current.copy(replyCount = (current.replyCount + delta).coerceAtLeast(0))
            commentStatisticsMapper.updateById(updated)
        }
    }
}
