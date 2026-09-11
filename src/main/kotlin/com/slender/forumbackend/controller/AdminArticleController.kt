package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.request.AdminArticleListRequest
import com.slender.forumbackend.model.request.ArticleUpdateRequest
import com.slender.forumbackend.facade.AdminArticleFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated
import org.springdoc.core.annotations.ParameterObject

@RestController
@RequestMapping("/admin/articles")
@PreAuthorize("hasAuthority('article:manage')")
class AdminArticleController(
    private val facade: AdminArticleFacade,
) {
    @GetMapping
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminArticleListRequest,
    ): Response<AdminPageData<Article>> =
        success(facade.list(request.page, request.size, request.includeDeleted, request.authorId, request.status))

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long
    ): Response<Article> = success(facade.get(id))

    @PutMapping("/{id}")
    fun update(
        @PathVariable
        id: Long,

        @Validated
        @RequestBody
        request: ArticleUpdateRequest,

        @AuthenticationPrincipal
        operator: UserCache,
    ): Response<Article> =
        success(facade.update(id, operator.uid, operator.authorities, request))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable
        id: Long,

        @AuthenticationPrincipal
        operator: UserCache,
    ): Response<Unit> {
        facade.delete(id, operator.uid, operator.authorities)
        return success()
    }
}
