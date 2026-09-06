package com.slender.forumbackend.component.common

import com.slender.forumbackend.constant.core.Redis
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.component.common.StatisticPendingReader.Companion.toPendingBoolean
import com.slender.forumbackend.component.common.StatisticPendingReader.Companion.toPendingTarget
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive
import org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization
import java.time.LocalDateTime

@Component
class InteractionPendingSyncExecutor(
    private val articleInteractionRepository: ArticleInteractionRepository,
    private val pendingReader: StatisticPendingReader,
    private val pendingWriter: StatisticPendingWriter,
) {
    interface LikeStatisticRepository {
        fun targetExists(targetId: Long): Boolean
        fun hasLike(targetId: Long, userId: Long): Boolean
        fun insertLike(targetId: Long, userId: Long, createTime: LocalDateTime)
        fun deleteLike(targetId: Long, userId: Long): Int
        fun adjustLikeCount(targetId: Long, delta: Int)
    }

    @Transactional(propagation = REQUIRES_NEW)
    fun syncPendingLikes(repository: LikeStatisticRepository, pendingKey: String) {
        for ((field, value) in pendingReader.pendingEntries(pendingKey)) {
            val target = field.toPendingTarget()
            val desiredLiked = value.toPendingBoolean()
            if (target == null || desiredLiked == null || !repository.targetExists(target.targetId)) {
                pendingWriter.deletePendingValueIfUnchanged(pendingKey, field, value)
                continue
            }
            val likedInDatabase = repository.hasLike(target.targetId, target.userId)
            when {
                desiredLiked && !likedInDatabase -> {
                    repository.insertLike(target.targetId, target.userId, LocalDateTime.now())
                    repository.adjustLikeCount(target.targetId, 1)
                }

                !desiredLiked && likedInDatabase -> {
                    repository.deleteLike(target.targetId, target.userId)
                    repository.adjustLikeCount(target.targetId, -1)
                }
            }
            acknowledgeAfterCommit(pendingKey, field, value)
        }
    }

    @Transactional(propagation = REQUIRES_NEW)
    fun syncArticleReaction(
        target: PendingTarget,
        field: String,
        pendingValue: String,
        emoji: String,
    ) {
        articleInteractionRepository.upsertReaction(target.targetId, target.userId, emoji, LocalDateTime.now())
        acknowledgeAfterCommit(Redis.Key.ARTICLE_REACTION_PENDING, field, pendingValue)
    }

    private fun acknowledgeAfterCommit(key: String, field: String, pendingValue: String) {
        check(isSynchronizationActive())
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                pendingWriter.deletePendingValueIfUnchanged(key, field, pendingValue)
            }
        })
    }
}
