package com.slender.forumbackend.service.admin

import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.repository.article.ArticlePromotionRepository
import org.springframework.stereotype.Service

@Service
class AdminArticlePromotionQueryService(
    private val repository: ArticlePromotionRepository,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean, articleId: Long?): AdminPageData<ArticlePromotion> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return AdminPageData(
            repository.listAdmin(safePage, safeSize, includeDeleted, articleId),
            safePage,
            safeSize,
            repository.countAdmin(includeDeleted, articleId),
        )
    }

    fun get(id: Long): ArticlePromotion =
        repository.findById(id) ?: throw InvalidRequestException("推广记录不存在")
}
