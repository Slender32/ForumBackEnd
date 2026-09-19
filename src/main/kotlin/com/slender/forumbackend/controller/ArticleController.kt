package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.ArticleFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.article.ArticleDetailData
import com.slender.forumbackend.model.data.article.ArticleListData
import com.slender.forumbackend.model.data.article.ArticlePublishData
import com.slender.forumbackend.model.data.article.ArticleRewardData
import com.slender.forumbackend.model.data.article.SearchSuggestionData
import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.request.ArticlePromotionRequest
import com.slender.forumbackend.model.request.ArticlePublishRequest
import com.slender.forumbackend.model.request.ArticleReactionRequest
import com.slender.forumbackend.model.request.ArticleRewardRequest
import com.slender.forumbackend.model.request.ArticleSearchRequest
import com.slender.forumbackend.model.request.ArticleUpdateRequest
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.model.request.SearchSuggestionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
@Tag(
    name = "Article",
    description = "Article feed, content, publish, and interaction APIs",
)
class ArticleController(
    private val articleFacade: ArticleFacade,
) {

    @PutMapping("/{aid}")
    @Operation(summary = "Update article", description = "Update article content and tags.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "1008 Invalid request; 1206 Invalid article tags"),
            ApiResponse(responseCode = "401", description = "Token missing or expired"),
            ApiResponse(responseCode = "404", description = "1201 Article not found"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @PathVariable
        aid: Long,

        @RequestBody
        @Validated
        request: ArticleUpdateRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.update(aid, userCache.uid, userCache.authorities, request)
        return success()
    }

    @DeleteMapping("/{aid}")
    fun delete(
        @PathVariable
        aid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.delete(aid, userCache.uid, userCache.authorities)
        return success()
    }

    @PostMapping("/{aid}/promotion")
    @Operation(summary = "推荐文章", description = "普通登录用户可为文章添加推荐内容。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "1008 Invalid request"),
            ApiResponse(responseCode = "401", description = "Token missing or expired"),
            ApiResponse(responseCode = "404", description = "1201 Article not found"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun addPromotion(
        @PathVariable
        aid: Long,

        @RequestBody
        @Validated
        request: ArticlePromotionRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.addPromotion(aid, userCache.uid, request)
        return success()
    }

    @DeleteMapping("/promotion/{pid}")
    fun deletePromotion(
        @PathVariable
        pid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.deletePromotion(pid, userCache.authorities)
        return success()
    }

    @PostMapping("/{aid}/tags/{tid}")
    fun restoreTag(
        @PathVariable
        aid: Long,

        @PathVariable
        tid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.restoreTag(aid, tid, userCache.uid, userCache.authorities)
        return success()
    }

    @DeleteMapping("/{aid}/tags/{tid}")
    fun deleteTag(
        @PathVariable
        aid: Long,

        @PathVariable
        tid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.deleteTag(aid, tid, userCache.uid, userCache.authorities)
        return success()
    }

    @GetMapping("/list")
    @Operation(
        summary = "Get article list",
        description = "Fetch public published articles by cursor pagination. First request uses cursorArticleId=-1 without cursorPublishTime. Supports anonymous access.",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "400", description = "1008 Invalid request"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        articleListRequest: ArticleListRequest,

        @AuthenticationPrincipal
        userCache: UserCache?
    ): Response<ArticleListData> {
        val data = articleFacade.list(articleListRequest, userCache?.uid)
        return success(data)
    }

    @GetMapping("/search")
    @Operation(summary = "搜索文章", description = "按标题和摘要 ILIKE 搜索已发布公开文章，按时间倒序分页。支持匿名访问。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "400", description = "1008 Invalid request"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun search(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: ArticleSearchRequest,

        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<ArticleListData> {
        val data = articleFacade.search(request, userCache?.uid)
        return success(data)
    }

    @GetMapping("/search/suggestion")
    @Operation(
        summary = "搜索建议",
        description = "keyword 为空返回热门词。支持匿名访问。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Success"
    )
    @SecurityRequirement(name = "bearerAuth")
    fun suggestions(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: SearchSuggestionRequest
    ): Response<SearchSuggestionData> {
        return success(articleFacade.suggestions(request))
    }

    @GetMapping("/{aid}")
    @Operation(
        summary = "Get article detail",
        description =
            "Return full article detail including content, author, tags, statistics, and reactions. Supports anonymous access.",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun content(
        @Parameter(description = "Article id")
        @PathVariable aid: Long,

        @AuthenticationPrincipal
        userCache: UserCache?,

        request: HttpServletRequest,
    ): Response<ArticleDetailData> {
        val data = articleFacade.detail(aid, userCache?.uid, request.remoteAddr)
        return success(data)
    }

    @PostMapping("/publish")
    @Operation(
        summary = "Publish article",
        description =
            "Create a public published article for the current user. Missing or blank summary is generated from Markdown content. Tags are reused by identical name and color.",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Published successfully"),
                ApiResponse(responseCode = "400", description = "1008 Invalid request; 1206 Invalid article tags"),
                ApiResponse(responseCode = "401", description = "Token missing or expired"),
                ApiResponse(responseCode = "500", description = "1500 Internal error"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun publish(
        @RequestBody
        @Validated
        request: ArticlePublishRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ArticlePublishData> {
        val data = articleFacade.publish(userCache.uid, request)
        return success(data)
    }

    @PostMapping("/{aid}/like")
    @Operation(
        summary = "Toggle article like",
        description = "Toggle current user's like status for an article.",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "401", description = "Token missing or expired"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun toggleLike(
        @PathVariable
        @Parameter(description = "Article id")
        aid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.toggleLike(aid, userCache.uid)
        return success()
    }

    @PostMapping("/{aid}/reaction")
    @Operation(
        summary = "React to article",
        description = "Add an emoji reaction. The same user may add multiple different emojis, but each emoji only once.",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "400", description = "1008 Invalid request"),
                ApiResponse(responseCode = "401", description = "Token missing or expired"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun react(
        @PathVariable
        @Parameter(description = "Article id")
        aid: Long,

        @RequestBody
        @Validated
        request: ArticleReactionRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.react(aid, userCache.uid, request.emoji)
        return success()
    }

    @DeleteMapping("/{aid}/reaction")
    fun deleteReaction(
        @PathVariable
        @Parameter(description = "Article id")
        aid: Long,

        @RequestBody
        @Validated
        request: ArticleReactionRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.deleteReaction(aid, userCache.uid, request.emoji)
        return success()
    }

    @PostMapping("/{aid}/reward")
    @Operation(
        summary = "打赏文章",
        description = "同一用户对同一篇文章只能打赏一次。需要登录。"
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(
                    responseCode = "400",
                    description =
                        "1008/1204/1205 Invalid amount, already rewarded or not enough moe point",
                ),
                ApiResponse(responseCode = "401", description = "Token missing or expired"),
                ApiResponse(responseCode = "403", description = "1009 Cannot reward own article"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun reward(
        @PathVariable
        @Parameter(description = "Article id")
        aid: Long,

        @RequestBody
        @Validated
        request: ArticleRewardRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ArticleRewardData> {
        val data = articleFacade.reward(aid, userCache.uid, request.amount)
        return success(data)
    }

    @PostMapping("/{aid}/report")
    @Operation(summary = "举报文章", description = "同一人对同一文章只能举报一次。需要登录。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "400", description = "1008/1602 Invalid request"),
                ApiResponse(responseCode = "401", description = "Token missing or expired"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
                ApiResponse(responseCode = "409", description = "1601 Duplicated report"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun report(
        @PathVariable
        @Parameter(description = "Article id")
        aid: Long,

        @RequestBody
        @Validated
        request: ReportRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.report(aid, userCache.uid, request)
        return success()
    }

    @PostMapping("/{aid}/favorite")
    @Operation(summary = "切换收藏", description = "已收藏则取消，未收藏则收藏。需要登录。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Success"),
                ApiResponse(responseCode = "401", description = "Token missing or expired"),
                ApiResponse(responseCode = "404", description = "1201 Article not found"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun toggleFavorite(
        @PathVariable
        @Parameter(description = "Article id")
        aid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        articleFacade.toggleFavorite(aid, userCache.uid)
        return success()
    }
}
