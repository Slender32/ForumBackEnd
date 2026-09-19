package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminReportFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.report.Report
import com.slender.forumbackend.model.request.AdminPageRequest
import com.slender.forumbackend.model.request.AdminReportUpdateRequest
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "AdminReport", description = "管理端举报处理")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasAuthority('report:manage')")
class AdminReportController(
    private val facade: AdminReportFacade
) {
    @GetMapping
    @Operation(summary = "查询举报", description = "需要登录并具有 report:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @ParameterObject
        @Validated request: AdminPageRequest
    ): Response<AdminPageData<Report>> =
        success(facade.list(request.page, request.size, request.includeDeleted))

    @GetMapping("/{id}")
    @Operation(summary = "查询详情：举报", description = "需要登录并具有 report:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun get(
        @Parameter(description = "举报 ID")
        @PathVariable id: Long
    ): Response<Report> = success(facade.get(id))

    @PutMapping("/{id}")
    @Operation(summary = "修改举报", description = "需要登录并具有 report:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun update(
        @Parameter(description = "举报 ID")
        @PathVariable id: Long,
        @Validated @RequestBody request: AdminReportUpdateRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal user: UserCache,
    ): Response<Report> = success(facade.update(id, user.uid, request))

    @DeleteMapping("/{id}")
    @Operation(summary = "删除举报", description = "需要登录并具有 report:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "举报 ID")
        @PathVariable id: Long
    ): Response<Unit> {
        facade.delete(id)
        return success()
    }
}
