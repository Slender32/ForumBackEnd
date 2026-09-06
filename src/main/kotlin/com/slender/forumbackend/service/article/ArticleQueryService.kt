package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.article.ArticleDetailAssembler
import com.slender.forumbackend.component.article.ArticleListAssembler
import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.exception.ArticleTagInvalidException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.library.toLocalDateTime
import com.slender.forumbackend.model.data.article.ArticleContentData
import com.slender.forumbackend.model.data.article.ArticleCursorData
import com.slender.forumbackend.model.data.article.ArticleDetailData
import com.slender.forumbackend.model.data.article.ArticleListData
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.request.ArticleSearchRequest
import com.slender.forumbackend.model.request.FavoriteListRequest
import com.slender.forumbackend.repository.article.ArticleContentRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.FavoriteRepository
import com.slender.forumbackend.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class ArticleQueryService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleContentRepository: ArticleContentRepository,
    private val favoriteRepository: FavoriteRepository,
    private val tagRepository: TagRepository,
    private val articleListAssembler: ArticleListAssembler,
    private val articleDetailAssembler: ArticleDetailAssembler,
) {
    fun list(request: ArticleListRequest, currentUserId: Long?): ArticleListData {
        if (request.tagId != null) tagRepository.findById(request.tagId) ?: throw ArticleTagInvalidException()
        return articleQueryRepository.findVisibleArticlePage(
            cursorPublishTime = request.cursorPublishTime?.toLocalDateTime(),
            cursorArticleId = request.cursorArticleId,
            limit = request.size + 1,
            tagId = request.tagId,
        ).toListData(request.size, currentUserId)
    }

    fun listByAuthor(authorId: Long, request: ArticleListRequest, currentUserId: Long?): ArticleListData {
        return articleQueryRepository.findVisibleArticlePage(
            cursorPublishTime = request.cursorPublishTime?.toLocalDateTime(),
            cursorArticleId = request.cursorArticleId,
            limit = request.size + 1,
            authorId = authorId,
        ).toListData(request.size, currentUserId)
    }

    fun listFavorites(userId: Long, request: FavoriteListRequest, currentUserId: Long?): ArticleListData {
        val fetched = favoriteRepository.findPageByUser(
            userId = userId,
            cursorFavoriteId = request.cursorFavoriteId,
            cursorCreateTime = request.cursorCreateTime?.toLocalDateTime(),
            limit = request.size + 1,
        )
        val hasMore = fetched.size > request.size
        val favorites = fetched.take(request.size)
        val articlesById = articleQueryRepository.findVisibleByIds(favorites.map { it.articleId })
            .associateBy { it.articleId }
        val articles = favorites.mapNotNull { articlesById[it.articleId] }
        return ArticleListData(
            items = articleListAssembler.assemble(articles, currentUserId),
            nextCursor = if (hasMore) favorites.lastOrNull()?.let {
                ArticleCursorData(
                    articleId = it.favoriteId,
                    publishTime = it.createTime.timestamp,
                )
            } else null,
            hasMore = hasMore,
        )
    }

    fun search(request: ArticleSearchRequest, currentUserId: Long?): ArticleListData {
        val keyword = request.keyword.trim()
        if (keyword.isEmpty()) throw InvalidRequestException("搜索关键词不能为空")
        val fetchedArticles = articleQueryRepository.findVisibleArticlePage(
            request.cursorPublishTime?.toLocalDateTime(),
            request.cursorArticleId,
            request.size + 1,
            keyword,
        )
        return fetchedArticles.toListData(request.size, currentUserId)
    }

    fun content(articleId: Long): ArticleContentData {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        val content = articleContentRepository.findContentById(articleId) ?: throw ArticleNotFoundException()
        return ArticleContentData(content.content)
    }

    fun detail(articleId: Long, currentUserId: Long?, viewerKey: String): ArticleDetailData {
        val article = articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        val content = articleContentRepository.findContentById(articleId) ?: throw ArticleNotFoundException()
        return articleDetailAssembler.assemble(article, content, currentUserId, viewerKey)
    }

    private fun List<Article>.toListData(pageSize: Int, currentUserId: Long?): ArticleListData {
        val hasMore = size > pageSize
        val articles = take(pageSize)
        return ArticleListData(
            items = articleListAssembler.assemble(articles, currentUserId),
            nextCursor = if (hasMore) articles.lastOrNull()?.toCursorData() else null,
            hasMore = hasMore,
        )
    }

}
