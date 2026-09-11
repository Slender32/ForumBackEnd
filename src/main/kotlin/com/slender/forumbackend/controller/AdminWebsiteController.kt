package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.request.WebsiteIntroductionRequest
import com.slender.forumbackend.model.request.WebsiteReleaseRequest
import com.slender.forumbackend.model.request.AdminPageRequest
import com.slender.forumbackend.facade.AdminWebsiteFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated
import org.springdoc.core.annotations.ParameterObject

@RestController
@RequestMapping("/admin/website")
@PreAuthorize("hasAuthority('website:manage')")
@Tag(name = "Admin Website", description = "Website introductions and releases management")
class AdminWebsiteController(
    private val facade: AdminWebsiteFacade,
) {
    @GetMapping("/introductions")
    @Operation(summary = "分页查询 Website 介绍")
    fun intros(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminPageRequest,
    ) = success(facade.introductions(request.page, request.size, request.includeDeleted))

    @GetMapping("/introductions/{id}")
    @Operation(summary = "查询 Website 介绍详情")
    fun intro(
        @PathVariable id: Long
    ) = success(facade.introduction(id))

    @PostMapping("/introductions")
    @Operation(summary = "新增 Website 介绍")
    fun addIntro(
        @Validated @RequestBody r: WebsiteIntroductionRequest,
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.addIntro(r, u.uid))

    @PutMapping("/introductions/{id}")
    @Operation(summary = "修改或启停 Website 介绍")
    fun updIntro(
        @PathVariable id: Long,
        @Validated @RequestBody r: WebsiteIntroductionRequest,
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.updateIntro(id, r, u.uid))

    @DeleteMapping("/introductions/{id}")
    @Operation(summary = "软删除 Website 介绍")
    fun delIntro(
        @PathVariable id: Long,
        @AuthenticationPrincipal u: UserCache
    ): Response<Int> =
        success(facade.deleteIntro(id, u.uid))

    @GetMapping("/releases")
    @Operation(summary = "分页查询 Website 版本")
    fun releases(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminPageRequest,
    ) = success(facade.releases(request.page, request.size, request.includeDeleted))

    @GetMapping("/releases/{id}")
    @Operation(summary = "查询 Website 版本详情")
    fun release(
        @PathVariable id: Long
    ) = success(facade.release(id))

    @PostMapping("/releases")
    @Operation(summary = "新增 Website 版本")
    fun addRel(
        @Validated @RequestBody r: WebsiteReleaseRequest,
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.addRelease(r, u.uid))

    @PutMapping("/releases/{id}")
    @Operation(summary = "修改或启停 Website 版本")
    fun updRel(
        @PathVariable id: Long,
        @Validated @RequestBody r: WebsiteReleaseRequest,
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.updateRelease(id, r, u.uid))

    @DeleteMapping("/releases/{id}")
    @Operation(summary = "软删除 Website 版本")
    fun delRel(
        @PathVariable id: Long,
        @AuthenticationPrincipal u: UserCache
    ): Response<Int> =
        success(facade.deleteRelease(id, u.uid))
}
