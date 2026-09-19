package com.slender.forumbackend.component.article

import com.slender.forumbackend.model.request.ArticlePublishTagRequest
import com.slender.forumbackend.exception.ArticleTagInvalidException
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ArticleTagWriter(
    private val tags: TagRepository,
    private val relations: ArticleTagRepository,
) {
    fun replace(articleId: Long, requested: List<ArticlePublishTagRequest>, now: LocalDateTime) {
        if (requested.size > TAG_LIMIT)
            throw ArticleTagInvalidException("文章标签数量不能超过 $TAG_LIMIT")
        if (requested.any { it.name.isBlank() || it.name.length > TAG_NAME_LIMIT })
            throw ArticleTagInvalidException("文章标签名称不能为空且长度不能超过 $TAG_NAME_LIMIT")

        val normalized = requested.map { it.copy(name = it.name.trim()) }.distinctBy { it.name to it.color }
        val ids = normalized.map { tags.findOrCreate(it.name, it.color, now).tid }.toSet()
        val current = relations.findTagsByArticleIds(listOf(articleId)).map { it.tagId }.toSet()
        (current - ids).forEach { relations.markDeleted(articleId, it, now) }
        (ids - current).forEach { relations.bindTag(articleId, it, now) }
    }

    private companion object {
        const val TAG_LIMIT = 10
        const val TAG_NAME_LIMIT = 64
    }
}
