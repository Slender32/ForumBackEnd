package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminWebsiteFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.request.AdminPageRequest
import com.slender.forumbackend.model.request.WebsiteIntroductionRequest
import com.slender.forumbackend.model.request.WebsiteReleaseRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
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

@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/website")
@PreAuthorize("hasAuthority('website:manage')")
@Tag(name = "Admin Website", description = "Website introductions and releases management")
class AdminWebsiteController(
    private val facade: AdminWebsiteFacade,
) {
    @GetMapping("/introductions")
    @Operation(summary = "分页查询 Website 介绍", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun intros(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminPageRequest,
    ) = success(facade.introductions(request.page, request.size, request.includeDeleted))

    @GetMapping("/introductions/{id}")
    @Operation(summary = "查询 Website 介绍详情", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun intro(
        @Parameter(description = "记录 ID")
        @PathVariable id: Long
    ) = success(facade.introduction(id))

    @PostMapping("/introductions")
    @Operation(summary = "新增 Website 介绍", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun addIntro(
        @Validated @RequestBody r: WebsiteIntroductionRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.addIntro(r, u.uid))

    @PutMapping("/introductions/{id}")
    @Operation(summary = "修改或启停 Website 介绍", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun updIntro(
        @Parameter(description = "记录 ID")
        @PathVariable id: Long,
        @Validated @RequestBody r: WebsiteIntroductionRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.updateIntro(id, r, u.uid))

    @DeleteMapping("/introductions/{id}")
    @Operation(summary = "软删除 Website 介绍", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delIntro(
        @Parameter(description = "记录 ID")
        @PathVariable id: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache
    ): Response<Int> =
        success(facade.deleteIntro(id, u.uid))

    @GetMapping("/releases")
    @Operation(summary = "分页查询 Website 版本", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun releases(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminPageRequest,
    ) = success(facade.releases(request.page, request.size, request.includeDeleted))

    @GetMapping("/releases/{id}")
    @Operation(summary = "查询 Website 版本详情", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun release(
        @Parameter(description = "记录 ID")
        @PathVariable id: Long
    ) = success(facade.release(id))

    @PostMapping("/releases")
    @Operation(summary = "新增 Website 版本", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun addRel(
        @Validated @RequestBody r: WebsiteReleaseRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.addRelease(r, u.uid))

    @PutMapping("/releases/{id}")
    @Operation(summary = "修改或启停 Website 版本", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun updRel(
        @Parameter(description = "记录 ID")
        @PathVariable id: Long,
        @Validated @RequestBody r: WebsiteReleaseRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.updateRelease(id, r, u.uid))

    @DeleteMapping("/releases/{id}")
    @Operation(summary = "软删除 Website 版本", description = "需要登录并具有 website:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delRel(
        @Parameter(description = "记录 ID")
        @PathVariable id: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache
    ): Response<Int> =
        success(facade.deleteRelease(id, u.uid))
}
