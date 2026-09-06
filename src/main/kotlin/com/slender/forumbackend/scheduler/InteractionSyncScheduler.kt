package com.slender.forumbackend.scheduler

import com.slender.forumbackend.service.article.ArticleStatisticSyncService
import com.slender.forumbackend.service.comment.CommentInteractionService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InteractionSyncScheduler(
    private val articleStatisticSyncService: ArticleStatisticSyncService,
    private val commentInteractionService: CommentInteractionService,
) {
    @Scheduled(fixedDelayString = $$"${forum.interaction.sync-delay-ms:5000}")
    fun syncPendingInteractions() {
        articleStatisticSyncService.syncPendingInteractions()
        commentInteractionService.syncPendingLikes()
    }
}
