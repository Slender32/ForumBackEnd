package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminAuditArchiveFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.request.ArchiveRecordListRequest
import com.slender.forumbackend.model.request.AuditLogListRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Audit and Archive", description = "审计日志与归档管理")
class AdminAuditArchiveController(
    private val facade: AdminAuditArchiveFacade,
) {
    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "分页查询审计日志", description = "需要登录并具有 audit:read 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun audits(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AuditLogListRequest,
    ) = success(facade.audits(request.page, request.size, request.result, request.resourceType))

    @GetMapping("/archive-records")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "分页查询归档记录", description = "需要登录并具有 archive:read 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun archives(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: ArchiveRecordListRequest,
    ) = success(facade.archives(request.page, request.size, request.status, request.sourceTable))

    @GetMapping("/archive-records/{id}")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "查询归档记录详情", description = "需要登录并具有 archive:read 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun archive(
        @Parameter(description = "归档记录 ID")
        @PathVariable id: Long
    ): Response<Map<String, Any?>> = success(facade.archive(id))

    @GetMapping("/archive-records/batches/{batchId}")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "查询归档批次详情", description = "需要登录并具有 archive:read 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun batch(
        @Parameter(description = "归档批次 ID")
        @PathVariable batchId: String
    ): Response<List<Map<String, Any?>>> =
        success(facade.batch(batchId))

    @PostMapping("/archive-records/retry")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "重试所有失败归档记录", description = "需要登录并具有 archive:read 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun retry(
        @Parameter(hidden = true)
        @AuthenticationPrincipal user: UserCache
    ): Response<Long> =
        success(facade.retryFailed(user.uid))
}
