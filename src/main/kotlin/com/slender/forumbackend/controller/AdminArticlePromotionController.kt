package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.request.AdminArticlePromotionListRequest
import com.slender.forumbackend.model.request.ArticlePromotionRequest
import com.slender.forumbackend.facade.AdminArticlePromotionFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated
import org.springdoc.core.annotations.ParameterObject

@RestController
@RequestMapping("/admin/article-promotions")
@PreAuthorize("hasAuthority('article:manage')")
class AdminArticlePromotionController(
    private val facade: AdminArticlePromotionFacade,
) {
    @GetMapping
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminArticlePromotionListRequest,
    ): Response<AdminPageData<ArticlePromotion>> =
        success(facade.list(request.page, request.size, request.includeDeleted, request.articleId))

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Response<ArticlePromotion> = success(facade.get(id))

    @PostMapping("/{articleId}")
    fun add(
        @PathVariable articleId: Long,
        @Validated @RequestBody request: ArticlePromotionRequest,
        @AuthenticationPrincipal operator: UserCache,
    ): Response<ArticlePromotion> =
        success(facade.add(articleId, operator.uid, request))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal operator: UserCache,
    ): Response<Unit> {
        facade.delete(id, operator.authorities)
        return success()
    }
}
