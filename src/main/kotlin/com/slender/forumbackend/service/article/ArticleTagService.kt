package com.slender.forumbackend.service.article

import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.exception.ArticleTagInvalidException
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class ArticleTagService(
    private val articles: ArticleQueryRepository,
    private val tags: TagRepository,
    private val relations: ArticleTagRepository,
) {
    fun restore(articleId: Long, tagId: Long, userId: Long, authorities: Set<String>) {
        authorize(articleId, userId, authorities)
        if (tags.findById(tagId) == null) throw ArticleTagInvalidException()
        relations.bindTag(articleId, tagId, now())
    }

    fun delete(articleId: Long, tagId: Long, userId: Long, authorities: Set<String>) {
        authorize(articleId, userId, authorities)
        relations.markDeleted(articleId, tagId, now())
    }

    private fun authorize(articleId: Long, userId: Long, authorities: Set<String>) {
        val article =
            articles.findById(articleId)?.takeIf {
                it.status != ArticleStatus.Deleted && it.deletedAt == null
            } ?: throw IllegalArgumentException("文章不存在")
        if (article.authorId != userId && "article:manage" !in authorities) {
            throw AccessDeniedException("NO_PERMISSION")
        }
    }
}