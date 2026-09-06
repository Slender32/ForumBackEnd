package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.model.entity.article.content.Article
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ArticleQueryRepository(
    private val articleMapper: ArticleMapper
) : ServiceImpl<ArticleMapper, Article>(), IService<Article> {

    fun findVisibleArticleById(articleId: Long): Article?
        = articleMapper.selectVisibleById(articleId)

    fun findVisibleArticleByIdOrThrow(articleId: Long): Article
        = findVisibleArticleById(articleId) ?: throw ArticleNotFoundException()

    fun findPublishedArticleById(articleId: Long): Article?
        = findVisibleArticleById(articleId)

    fun findPublishedArticleByIdOrThrow(articleId: Long): Article
        = findPublishedArticleById(articleId) ?: throw ArticleNotFoundException()

    fun targetExists(targetId: Long): Boolean
        = findVisibleArticleById(targetId) != null

    fun findVisibleArticlePage(
        cursorPublishTime: LocalDateTime?,
        cursorArticleId: Long,
        limit: Int,
        tagId: Long? = null,
        authorId: Long? = null,
        articleIds: List<Long>? = null
    ) = articleMapper.selectVisiblePage(
            cursorPublishTime,
            cursorArticleId,
            limit,
            tagId,
            authorId,
            articleIds?.distinct()?.takeIf { it.isNotEmpty() }
        )

    fun findVisibleArticlePage(
        cursorPublishTime: LocalDateTime?,
        cursorArticleId: Long,
        limit: Int,
        keyword: String
    ) = articleMapper.selectVisiblePageByKeyword(
            cursorPublishTime,
            cursorArticleId,
            limit,
            "%${keyword.escapeLike()}%"
        )

    fun findVisibleByIds(articleIds: List<Long>)
        = if (articleIds.isEmpty()) emptyList()
            else articleMapper.selectVisibleByIds(articleIds.distinct())

    fun findByIds(articleIds: List<Long>) =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let { articleMapper.selectByIds(it) } ?: emptyList()

    fun findTitleSuggestions(prefix: String, limit: Int)
        = articleMapper.selectTitleSuggestions("${prefix.escapeLike()}%", limit).map { it.title }

    fun findPopularTitles(limit: Int) = articleMapper.selectPopularTitles(limit).map { it.title }
}

private fun String.escapeLike() =
    this.replace("!", "!!")
        .replace("%", "!%")
        .replace("_", "!_")
