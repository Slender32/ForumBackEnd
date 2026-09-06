package com.slender.forumbackend.service.article

import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_LIKE_PENDING
import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_REACTION_PENDING
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.component.common.InteractionPendingSyncExecutor
import com.slender.forumbackend.component.common.StatisticPendingReader
import com.slender.forumbackend.component.common.StatisticPendingReader.Companion.toPendingTarget
import com.slender.forumbackend.component.common.StatisticPendingWriter
import org.springframework.stereotype.Service

@Service
class ArticleStatisticSyncService(
    private val articleInteractionRepository: ArticleInteractionRepository,
    private val pendingReader: StatisticPendingReader,
    private val pendingWriter: StatisticPendingWriter,
    private val syncExecutor: InteractionPendingSyncExecutor,
) {
    fun syncPendingInteractions() {
        syncExecutor.syncPendingLikes(articleInteractionRepository, ARTICLE_LIKE_PENDING)
        val entries = pendingReader.pendingEntries(ARTICLE_REACTION_PENDING)
        for ((field, value) in entries) {
            val target = field.toPendingTarget()
            val desiredEmoji = value.trim()
            if (
                target == null ||
                desiredEmoji.isBlank() ||
                desiredEmoji.length > EMOJI_LIMIT ||
                !articleInteractionRepository.targetExists(target.targetId)
            ) {
                pendingWriter.deletePendingValueIfUnchanged(ARTICLE_REACTION_PENDING, field, value)
                continue
            }
            syncExecutor.syncArticleReaction(target, field, value, desiredEmoji)
        }
    }

    private companion object {
        const val EMOJI_LIMIT = 8
    }
}
