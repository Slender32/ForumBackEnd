package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Deleted
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Draft
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Published
import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.model.entity.article.content.Article
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArticleQueryRepository(
    private val articleMapper: ArticleMapper
) : ServiceImpl<ArticleMapper, Article>(), IService<Article> {

    fun findById(articleId: Long): Article? = articleMapper.selectById(articleId)

    fun approve(articleId: Long, title: String?, summary: String?, cover: String?, now: LocalDateTime): Boolean {
        val current = findById(articleId) ?: return false
        if (current.deletedAt != null) return false
        return articleMapper.updateById(
            current.copy(
                title = title ?: current.title,
                summary = summary ?: current.summary,
                cover = cover ?: current.cover,
                status = Published,
                publishTime = if (current.publishTime == LocalDateTime.MIN) now else current.publishTime,
                updateTime = now,
            )
        ) == 1
    }

    fun rejectDraft(articleId: Long, now: LocalDateTime): Boolean {
        val current = findById(articleId) ?: return false
        if (current.deletedAt != null || current.status != Draft) return false
        return articleMapper.updateById(
            current.copy(
                status = Deleted,
                updateTime = now,
                deletedAt = now,
            )
        ) == 1
    }

    fun listAdmin(
        page: Int,
        size: Int,
        includeDeleted: Boolean = false,
        authorId: Long? = null,
        status: String? = null,
    ): List<Article> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        val query =
            QueryWrapper<Article>()
                .apply(if (includeDeleted) "1=1" else "deleted_at IS NULL AND status <> 'DELETED'")
                .eq(authorId != null, "author_id", authorId)
                .eq(status != null, "status", status)
                .orderByDesc("create_time")
                .orderByDesc("article_id")
                .last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        return articleMapper.selectList(query)
    }

    fun countAdmin(
        includeDeleted: Boolean = false,
        authorId: Long? = null,
        status: String? = null,
    ): Long =
        articleMapper.selectCount(
            QueryWrapper<Article>()
                .apply(if (includeDeleted) "1=1" else "deleted_at IS NULL AND status <> 'DELETED'")
                .eq(authorId != null, "author_id", authorId)
                .eq(status != null, "status", status)
        )

    fun findVisibleArticleById(articleId: Long): Article? =
        articleMapper.selectVisibleById(articleId)

    fun findVisibleArticleByIdOrThrow(articleId: Long): Article =
        findVisibleArticleById(articleId) ?: throw ArticleNotFoundException()

    fun findPublishedArticleById(articleId: Long): Article? = findVisibleArticleById(articleId)

    fun findPublishedArticleByIdOrThrow(articleId: Long): Article =
        findPublishedArticleById(articleId) ?: throw ArticleNotFoundException()

    fun targetExists(targetId: Long): Boolean = findVisibleArticleById(targetId) != null

    fun findVisibleArticlePage(
        cursorPublishTime: LocalDateTime?,
        cursorArticleId: Long,
        limit: Int,
        tagId: Long? = null,
        authorId: Long? = null,
        articleIds: List<Long>? = null,
    ) =
        articleMapper.selectVisiblePage(
            cursorPublishTime,
            cursorArticleId,
            limit,
            tagId,
            authorId,
            articleIds?.distinct()?.takeIf { it.isNotEmpty() },
        )

    fun findVisibleArticlePage(
        cursorPublishTime: LocalDateTime?,
        cursorArticleId: Long,
        limit: Int,
        keyword: String,
    ) =
        articleMapper.selectVisiblePageByKeyword(
            cursorPublishTime,
            cursorArticleId,
            limit,
            "%${keyword.escapeLike()}%",
        )

    fun findVisibleByIds(articleIds: List<Long>) =
        if (articleIds.isEmpty()) emptyList()
        else articleMapper.selectVisibleByIds(articleIds.distinct())

    fun findByIds(articleIds: List<Long>) =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let { articleMapper.selectByIds(it) }
            ?: emptyList()

    fun findTitleSuggestions(prefix: String, limit: Int) =
        articleMapper.selectTitleSuggestions("${prefix.escapeLike()}%", limit).map { it.title }

    fun findPopularTitles(limit: Int) = articleMapper.selectPopularTitles(limit).map { it.title }
}

private fun String.escapeLike() =
    this.replace("!", "!!")
        .replace("%", "!%")
        .replace("_", "!_")
