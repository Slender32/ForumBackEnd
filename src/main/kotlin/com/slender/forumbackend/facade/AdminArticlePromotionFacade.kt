package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.ArticlePromotionRequest
import com.slender.forumbackend.service.admin.AdminArticlePromotionQueryService
import com.slender.forumbackend.service.article.ArticlePromotionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminArticlePromotionFacade(
    private val query: AdminArticlePromotionQueryService,
    private val commands: ArticlePromotionService,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean, articleId: Long?) =
        query.list(page, size, includeDeleted, articleId)

    fun get(id: Long) = query.get(id)

    @Transactional
    fun add(articleId: Long, operatorId: Long, request: ArticlePromotionRequest) =
        commands.add(articleId, operatorId, request)

    @Transactional
    fun delete(id: Long, authorities: Set<String>) = commands.delete(id, authorities)
}
