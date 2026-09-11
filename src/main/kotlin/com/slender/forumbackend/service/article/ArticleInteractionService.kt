package com.slender.forumbackend.service.article

import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class ArticleInteractionService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleInteractionRepository: ArticleInteractionRepository,
) {
    fun toggleLike(articleId: Long, userId: Long) {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        if (articleInteractionRepository.hasLike(articleId, userId)) {
            articleInteractionRepository.deleteLike(articleId, userId)
            articleInteractionRepository.adjustLikeCount(articleId, -1)
        } else {
            articleInteractionRepository.insertLike(articleId, userId, LocalDateTime.now())
            articleInteractionRepository.adjustLikeCount(articleId, 1)
        }
    }

    fun react(articleId: Long, userId: Long, emoji: String) {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        articleInteractionRepository.upsertReaction(
            articleId,
            userId,
            normalizeEmoji(emoji),
            LocalDateTime.now(),
        )
    }

    fun deleteReaction(articleId: Long, userId: Long, emoji: String) {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        articleInteractionRepository.deleteReaction(
            articleId,
            userId,
            normalizeEmoji(emoji),
            LocalDateTime.now(),
        )
    }

    private fun normalizeEmoji(emoji: String): String {
        val trimmed = emoji.trim()
        if (trimmed.isEmpty() || trimmed.length > EMOJI_LIMIT)
            throw InvalidRequestException("表情不能为空或超过8个字符")
        return trimmed
    }

    private companion object {
        const val EMOJI_LIMIT = 8
    }
}