package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.slender.forumbackend.mapper.ArticleLikeMapper
import com.slender.forumbackend.mapper.ArticleReactionMapper
import com.slender.forumbackend.mapper.ArticleStatisticMapper
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleLike
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArticleInteractionRepository(
    private val articleStatisticMapper: ArticleStatisticMapper,
    private val articleLikeMapper: ArticleLikeMapper,
    private val articleReactionMapper: ArticleReactionMapper,
) {
    fun findLike(articleId: Long, userId: Long): ArticleLike? =
        articleLikeMapper.selectByArticleAndUser(articleId, userId)

    fun hasLike(articleId: Long, userId: Long): Boolean = findLike(articleId, userId) != null

    fun findLikesByUserAndArticleIds(userId: Long, articleIds: Collection<Long>): Set<Long> {
        if (articleIds.isEmpty()) return emptySet()
        return articleLikeMapper.selectArticleIdsByUser(userId, articleIds.distinct()).toSet()
    }

    fun insertLike(articleId: Long, userId: Long, createTime: LocalDateTime) {
        articleLikeMapper.upsertLike(articleId, userId, createTime)
    }

    fun deleteLike(articleId: Long, userId: Long): Int =
        articleLikeMapper.deleteByArticleAndUser(articleId, userId)

    fun adjustLikeCount(articleId: Long, delta: Int) {
        val current = articleStatisticMapper.selectById(articleId)
        val updated =
            (current ?: ArticleStatistic(articleId, 0, 0, 0, 0)).copy(
                likeCount = (current?.likeCount ?: 0) + delta
            )
        articleStatisticMapper.run { if (current == null) insert(updated) else updateById(updated) }
    }

    fun findReactionsByArticleIds(articleIds: Collection<Long>): List<ArticleReaction> =
        articleIds
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { articleReactionMapper.selectByArticleIds(it) } ?: emptyList()

    fun upsertReaction(articleId: Long, userId: Long, emoji: String, createTime: LocalDateTime) {
        articleReactionMapper.upsertReaction(articleId, userId, emoji, createTime)
    }

    fun deleteReaction(articleId: Long, userId: Long, emoji: String, now: LocalDateTime) =
        articleReactionMapper.update(
            null,
            UpdateWrapper<ArticleReaction>()
                .eq("article_id", articleId)
                .eq("user_id", userId)
                .eq("emoji", emoji)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )

    fun markDeletedByArticle(articleId: Long, now: LocalDateTime) {
        articleLikeMapper.update(
            null,
            UpdateWrapper<ArticleLike>()
                .eq("article_id", articleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
        articleReactionMapper.update(
            null,
            UpdateWrapper<ArticleReaction>()
                .eq("article_id", articleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
    }
}
