package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.ArticleUpdateRequest
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.service.admin.AdminArticleQueryService
import com.slender.forumbackend.service.article.ArticleCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminArticleFacade(
    private val query: AdminArticleQueryService,
    private val commands: ArticleCommandService,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean, authorId: Long?, status: String?) =
        query.list(page, size, includeDeleted, authorId, status)

    fun get(id: Long) = query.get(id)

    fun detail(id: Long) = query.detail(id)

    @Transactional
    fun update(id: Long, operatorId: Long, authorities: Set<String>, request: ArticleUpdateRequest): Article {
        commands.update(id, operatorId, authorities, request)
        return query.get(id)
    }

    @Transactional
    fun delete(id: Long, operatorId: Long, authorities: Set<String>) =
        commands.delete(id, operatorId, authorities)
}
