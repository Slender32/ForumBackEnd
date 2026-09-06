package com.slender.forumbackend.repository.article

import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_LIKE_PENDING
import com.slender.forumbackend.mapper.ArticleLikeMapper
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.mapper.ArticleReactionMapper
import com.slender.forumbackend.mapper.ArticleStatisticMapper
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleLike
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.toolkit.LikeStatisticRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ArticleInteractionRepository(
    private val articleMapper: ArticleMapper,
    private val articleStatisticMapper: ArticleStatisticMapper,
    private val articleLikeMapper: ArticleLikeMapper,
    private val articleReactionMapper: ArticleReactionMapper,
) : LikeStatisticRepository {
    override val pendingKey = ARTICLE_LIKE_PENDING

    override fun targetExists(targetId: Long): Boolean =
        articleMapper.selectVisibleById(targetId) != null

    fun findLike(articleId: Long, userId: Long): ArticleLike? =
        articleLikeMapper.selectByArticleAndUser(articleId, userId)

    override fun hasLike(targetId: Long, userId: Long): Boolean =
        findLike(targetId, userId) != null

    fun findLikesByUserAndArticleIds(userId: Long, articleIds: Collection<Long>): Set<Long> {
        if (articleIds.isEmpty()) return emptySet()
        return articleLikeMapper.selectArticleIdsByUser(userId, articleIds.distinct()).toSet()
    }

    override fun insertLike(targetId: Long, userId: Long, createTime: LocalDateTime) {
        articleLikeMapper.insert(
            ArticleLike(
                articleId = targetId,
                userId = userId,
                createTime = createTime,
            )
        )
    }

    override fun deleteLike(targetId: Long, userId: Long): Int =
        articleLikeMapper.deleteByArticleAndUser(targetId, userId)

    override fun adjustLikeCount(targetId: Long, delta: Int) {
        val current = articleStatisticMapper.selectById(targetId)
        val updated = (current ?: ArticleStatistic(targetId, 0, 0, 0, 0))
            .copy(likeCount = (current?.likeCount ?: 0) + delta)
        articleStatisticMapper.run {
            if (current == null) insert(updated)
            else updateById(updated)
        }
    }

    fun findReactionsByArticleIds(articleIds: Collection<Long>): List<ArticleReaction> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            articleReactionMapper.selectByArticleIds(it)
        } ?: emptyList()

    fun upsertReaction(
        articleId: Long,
        userId: Long,
        emoji: String,
        createTime: LocalDateTime,
    ) {
        articleReactionMapper.upsertReaction(articleId, userId, emoji, createTime)
    }
}
