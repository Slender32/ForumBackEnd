package com.slender.forumbackend.service.article

import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_REACTION_PENDING
import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_LIKE_PENDING
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.component.common.StatisticPendingWriter
import org.springframework.stereotype.Service

@Service
class ArticleInteractionService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleInteractionRepository: ArticleInteractionRepository,
    private val statisticPendingWriter: StatisticPendingWriter,
) {
    fun toggleLike(articleId: Long, userId: Long) {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        statisticPendingWriter.toggleLike(
            ARTICLE_LIKE_PENDING,
            articleId,
            userId,
            articleInteractionRepository.hasLike(articleId, userId),
        )
    }

    fun react(articleId: Long, userId: Long, emoji: String) {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        val trimmed = emoji.trim()
        if (trimmed.isEmpty() || trimmed.length > EMOJI_LIMIT)
            throw InvalidRequestException("表情不能为空或超过8个字符")
        statisticPendingWriter.putPending(ARTICLE_REACTION_PENDING, articleId, userId, trimmed)
    }

    private companion object {
        const val EMOJI_LIMIT = 8
    }
}
