package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.request.AdminCommentListRequest
import com.slender.forumbackend.model.request.CommentCreateRequest
import com.slender.forumbackend.facade.AdminCommentFacade
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
@RequestMapping("/admin/comments")
@PreAuthorize("hasAuthority('comment:manage')")
class AdminCommentController(
    private val facade: AdminCommentFacade,
) {
    @GetMapping
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminCommentListRequest,
    ): Response<AdminPageData<Comment>> =
        success(facade.list(
            request.page,
            request.size,
            request.includeDeleted,
            request.articleId,
            request.authorId)
        )

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long
    ): Response<Comment> = success(facade.get(id))

    @PutMapping("/{id}")
    fun replace(
        @PathVariable id: Long,
        @Validated @RequestBody request: CommentCreateRequest,
        @AuthenticationPrincipal operator: UserCache,
    ): Response<Comment> = success(facade.update(id, operator.uid, operator.authorities, request))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal operator: UserCache,
    ): Response<Unit> {
        facade.delete(id, operator.uid, operator.authorities)
        return success()
    }
}
