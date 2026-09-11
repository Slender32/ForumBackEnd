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
    private val articleCommandService: ArticleCommandService,
    private val articlePromotionService: ArticlePromotionService,
    private val articleTagService: ArticleTagService,
) {
    fun list(request: ArticleListRequest, currentUserId: Long?) =
        articleQueryService.list(request, currentUserId)

    fun search(request: ArticleSearchRequest, currentUserId: Long?) =
        articleQueryService.search(request, currentUserId)

    fun suggestions(request: SearchSuggestionRequest) =
        searchSuggestionService.suggestions(request)

    fun detail(articleId: Long, currentUserId: Long?, ip: String) =
        articleQueryService.detail(
            articleId,
            currentUserId,
            currentUserId?.let { "u:$it" } ?: "ip:$ip",
        )

    @Transactional
    fun publish(authorId: Long, request: ArticlePublishRequest) =
        articlePublishService.publish(authorId, request)

    @Transactional
    fun toggleLike(articleId: Long, userId: Long) =
        articleInteractionService.toggleLike(articleId, userId)

    @Transactional
    fun react(articleId: Long, userId: Long, emoji: String) =
        articleInteractionService.react(articleId, userId, emoji)

    @Transactional
    fun deleteReaction(articleId: Long, userId: Long, emoji: String) =
        articleInteractionService.deleteReaction(articleId, userId, emoji)

    @Transactional
    fun reward(articleId: Long, userId: Long, amount: Int) =
        articleRewardService.reward(articleId, userId, amount)

    fun report(articleId: Long, reporterId: Long, request: ReportRequest) =
        articleReportService.report(articleId, reporterId, request)

    @Transactional
    fun update(
        articleId: Long,
        userId: Long,
        authorities: Set<String>,
        request: ArticleUpdateRequest,
    ) = articleCommandService.update(articleId, userId, authorities, request)

    @Transactional
    fun delete(articleId: Long, userId: Long, authorities: Set<String>) =
        articleCommandService.delete(articleId, userId, authorities)

    fun addPromotion(
        articleId: Long,
        userId: Long,
        request: ArticlePromotionRequest,
    ) = articlePromotionService.add(articleId, userId, request)

    fun deletePromotion(id: Long, authorities: Set<String>) =
        articlePromotionService.delete(id, authorities)

    @Transactional
    fun restoreTag(articleId: Long, tagId: Long, userId: Long, authorities: Set<String>) =
        articleTagService.restore(articleId, tagId, userId, authorities)

    @Transactional
    fun deleteTag(articleId: Long, tagId: Long, userId: Long, authorities: Set<String>) =
        articleTagService.delete(articleId, tagId, userId, authorities)

    @Transactional
    fun toggleFavorite(articleId: Long, userId: Long) =
        articleFavoriteService.toggleFavorite(articleId, userId)
}
