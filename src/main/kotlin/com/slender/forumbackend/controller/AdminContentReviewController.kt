package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.request.AdminContentReviewListRequest
import com.slender.forumbackend.model.request.ContentReviewDecisionRequest
import com.slender.forumbackend.facade.AdminContentReviewFacade
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.governance.ContentReviewData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
@RequestMapping("/admin/content-reviews")
@PreAuthorize("hasAuthority('content-review:manage')")
@Tag(name = "Admin Content Reviews", description = "敏感内容审核")
class AdminContentReviewController(
    private val facade: AdminContentReviewFacade,
) {
    @GetMapping
    @Operation(summary = "审核任务列表")
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminContentReviewListRequest,
    ): Response<AdminPageData<ContentReviewData>> =
        success(facade.list(request.status, request.page, request.size))

    @GetMapping("/{id}")
    @Operation(summary = "审核任务详情")
    fun get(
        @PathVariable id: Long
    ): Response<ContentReviewData> = success(facade.get(id))

    @PutMapping("/{id}")
    @Operation(summary = "批准或拒绝审核任务")
    fun decide(
        @PathVariable id: Long,
        @Validated @RequestBody request: ContentReviewDecisionRequest,
        @AuthenticationPrincipal user: UserCache,
    ): Response<Int> = success(facade.decide(id, user.uid, request))

}
