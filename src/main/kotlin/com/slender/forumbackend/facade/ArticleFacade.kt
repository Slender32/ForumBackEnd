package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.*
import com.slender.forumbackend.service.article.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleFacade(
    private val articleQueryService: ArticleQueryService,
    private val articlePublishService: ArticlePublishService,
    private val articleInteractionService: ArticleInteractionService,
    private val articleRewardService: ArticleRewardService,
    private val articleFavoriteService: ArticleFavoriteService,
    private val searchSuggestionService: SearchSuggestionService,
    private val articleReportService: ArticleReportService,
) {
    fun list(request: ArticleListRequest, currentUserId: Long?) =
        articleQueryService.list(request, currentUserId)

    fun search(request: ArticleSearchRequest, currentUserId: Long?) =
        articleQueryService.search(request, currentUserId)

    fun suggestions(request: SearchSuggestionRequest) =
        searchSuggestionService.suggestions(request)

    fun detail(articleId: Long, currentUserId: Long?, ip: String) =
        articleQueryService.detail(articleId, currentUserId, currentUserId?.let { "u:$it" } ?: "ip:$ip")

    @Transactional
    fun publish(authorId: Long, request: ArticlePublishRequest) =
        articlePublishService.publish(authorId, request)

    fun toggleLike(articleId: Long, userId: Long) =
        articleInteractionService.toggleLike(articleId, userId)

    fun react(articleId: Long, userId: Long, emoji: String) =
        articleInteractionService.react(articleId, userId, emoji)

    @Transactional
    fun reward(articleId: Long, userId: Long, amount: Int) =
        articleRewardService.reward(articleId, userId, amount)

    fun report(articleId: Long, reporterId: Long, request: ReportRequest) =
        articleReportService.report(articleId, reporterId, request)

    @Transactional
    fun toggleFavorite(articleId: Long, userId: Long) =
        articleFavoriteService.toggleFavorite(articleId, userId)
}
