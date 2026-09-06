package com.slender.forumbackend.service.article

import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_REACTION_PENDING
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.service.InteractionPendingSyncExecutor
import com.slender.forumbackend.toolkit.StatisticPendingWriter.Companion.toPendingTarget
import com.slender.forumbackend.toolkit.StatisticPendingWriter.Companion.toPendingBoolean
import com.slender.forumbackend.toolkit.deletePendingValueIfUnchanged
import com.slender.forumbackend.toolkit.pendingEntries
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class ArticleStatisticSyncService(
    private val articleInteractionRepository: ArticleInteractionRepository,
    private val redisTemplate: StringRedisTemplate,
    private val syncExecutor: InteractionPendingSyncExecutor,
) {
    fun syncPendingInteractions() {
        syncPendingLikes()
        val entries = redisTemplate.pendingEntries(ARTICLE_REACTION_PENDING)
        for ((field, value) in entries) {
            val target = field.toPendingTarget()
            val desiredEmoji = value.trim()
            if (
                target == null ||
                desiredEmoji.isBlank() ||
                desiredEmoji.length > EMOJI_LIMIT ||
                !articleInteractionRepository.targetExists(target.targetId)
            ) {
                redisTemplate.deletePendingValueIfUnchanged(ARTICLE_REACTION_PENDING, field, value)
                continue
            }
            syncExecutor.syncArticleReaction(target, field, value, desiredEmoji)
        }
    }

    private fun syncPendingLikes() {
        val pendingKey = articleInteractionRepository.pendingKey
        for ((field, value) in redisTemplate.pendingEntries(pendingKey)) {
            val target = field.toPendingTarget()
            val desiredLiked = value.toPendingBoolean()
            if (target == null || desiredLiked == null || !articleInteractionRepository.targetExists(target.targetId)) {
                redisTemplate.deletePendingValueIfUnchanged(pendingKey, field, value)
                continue
            }
            syncExecutor.syncLike(articleInteractionRepository, target, field, value, desiredLiked)
        }
    }

    private companion object {
        const val EMOJI_LIMIT = 8
    }
}
