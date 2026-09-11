package com.slender.forumbackend.service.article

import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.request.ArticlePromotionRequest
import com.slender.forumbackend.repository.article.ArticlePromotionRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import java.time.LocalDateTime
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class ArticlePromotionService(
    private val articles: ArticleQueryRepository,
    private val promotions: ArticlePromotionRepository,
) {
    fun add(
        articleId: Long,
        userId: Long,
        request: ArticlePromotionRequest,
    ): ArticlePromotion {
        articles.findVisibleArticleByIdOrThrow(articleId)
        val item =
            ArticlePromotion(
                articleId = articleId,
                promoterId = userId,
                content = request.content.trim(),
                promoteTime = LocalDateTime.now(),
            )
        promotions.save(item)
        return item
    }

    fun delete(id: Long, authorities: Set<String>) {
        if ("article:manage" !in authorities) throw AccessDeniedException("NO_PERMISSION")
        promotions.markDeleted(id, LocalDateTime.now())
    }
}
