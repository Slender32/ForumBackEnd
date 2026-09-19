package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminContentReviewFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.governance.ContentReviewData
import com.slender.forumbackend.model.request.AdminContentReviewListRequest
import com.slender.forumbackend.model.request.ContentReviewDecisionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/content-reviews")
@PreAuthorize("hasAuthority('content-review:manage')")
@Tag(name = "Admin Content Reviews", description = "敏感内容审核")
class AdminContentReviewController(
    private val facade: AdminContentReviewFacade,
) {
    @GetMapping
    @Operation(summary = "审核任务列表", description = "需要登录并具有 content-review:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminContentReviewListRequest,
    ): Response<AdminPageData<ContentReviewData>> =
        success(facade.list(request.status, request.page, request.size))

    @GetMapping("/{id}")
    @Operation(summary = "审核任务详情", description = "需要登录并具有 content-review:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun get(
        @Parameter(description = "审核任务 ID")
        @PathVariable id: Long
    ): Response<ContentReviewData> = success(facade.get(id))

    @PutMapping("/{id}")
    @Operation(summary = "批准或拒绝审核任务")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1206 文章标签不合法"),
        ]
    )
    fun decide(
        @Parameter(description = "审核任务 ID")
        @PathVariable
        id: Long,

        @Validated
        @RequestBody
        request: ContentReviewDecisionRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        user: UserCache,
    ): Response<Int> =
        success(facade.decide(id, user.uid, request))

}
