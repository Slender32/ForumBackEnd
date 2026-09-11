package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.article.ArticleTagData
import com.slender.forumbackend.model.request.UserTagRequest
import com.slender.forumbackend.facade.UserTagFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated

/** Administrative alias for managing another user's tag relations. */
@RestController
@RequestMapping("/admin/users/{uid}/tags")
@PreAuthorize("hasAuthority('relation:manage')")
class AdminUserTagController(
    private val facade: UserTagFacade,
) {
    @GetMapping
    fun list(
        @PathVariable uid: Long
    ): Response<List<ArticleTagData>> = success(facade.list(uid))

    @PostMapping
    fun add(
        @PathVariable uid: Long,
        @Validated @RequestBody request: UserTagRequest,
        @AuthenticationPrincipal operator: UserCache,
    ): Response<Unit> {
        facade.add(uid, request, operator.uid, operator.authorities)
        return success()
    }

    @DeleteMapping("/{tagId}")
    fun delete(
        @PathVariable uid: Long,
        @PathVariable tagId: Long,
        @AuthenticationPrincipal operator: UserCache,
    ): Response<Unit> {
        facade.delete(uid, tagId, operator.uid, operator.authorities)
        return success()
    }
}
