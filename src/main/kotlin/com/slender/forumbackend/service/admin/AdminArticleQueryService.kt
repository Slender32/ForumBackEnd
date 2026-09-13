package com.slender.forumbackend.service.admin

import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.AdminArticleDetailData
import com.slender.forumbackend.repository.article.ArticleContentRepository
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import org.springframework.stereotype.Service

@Service
class AdminArticleQueryService(
    private val articles: ArticleQueryRepository,
    private val contents: ArticleContentRepository,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean, authorId: Long?, status: String?): AdminPageData<Article> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return AdminPageData(
            articles.listAdmin(safePage, safeSize, includeDeleted, authorId, status),
            safePage,
            safeSize,
            articles.countAdmin(includeDeleted, authorId, status),
        )
    }

    fun get(id: Long): Article = articles.findById(id) ?: throw ArticleNotFoundException()

    fun detail(id: Long) = AdminArticleDetailData(get(id), contents.getById(id)?.content)
}
