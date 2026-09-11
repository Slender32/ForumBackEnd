package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.request.ArchiveRecordListRequest
import com.slender.forumbackend.model.request.AuditLogListRequest
import com.slender.forumbackend.facade.AdminAuditArchiveFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated
import org.springdoc.core.annotations.ParameterObject

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Audit and Archive", description = "审计日志与归档管理")
class AdminAuditArchiveController(
    private val facade: AdminAuditArchiveFacade,
) {
    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "分页查询审计日志")
    fun audits(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AuditLogListRequest,
    ) = success(facade.audits(request.page, request.size, request.result, request.resourceType))

    @GetMapping("/archive-records")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "分页查询归档记录")
    fun archives(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: ArchiveRecordListRequest,
    ) = success(facade.archives(request.page, request.size, request.status, request.sourceTable))

    @GetMapping("/archive-records/{id}")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "查询归档记录详情")
    fun archive(
        @PathVariable id: Long
    ): Response<Map<String, Any?>> = success(facade.archive(id))

    @GetMapping("/archive-records/batches/{batchId}")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "查询归档批次详情")
    fun batch(
        @PathVariable batchId: String
    ): Response<List<Map<String, Any?>>> =
        success(facade.batch(batchId))

    @PostMapping("/archive-records/retry")
    @PreAuthorize("hasAuthority('archive:read')")
    @Operation(summary = "重试所有失败归档记录")
    fun retry(
        @AuthenticationPrincipal user: UserCache
    ): Response<Long> =
        success(facade.retryFailed(user.uid))
}
