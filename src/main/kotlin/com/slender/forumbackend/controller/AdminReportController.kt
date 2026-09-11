package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.entity.report.Report
import com.slender.forumbackend.model.request.AdminPageRequest
import com.slender.forumbackend.model.request.AdminReportUpdateRequest
import com.slender.forumbackend.facade.AdminReportFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated

@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasAuthority('report:manage')")
class AdminReportController(
    private val facade: AdminReportFacade
) {
    @GetMapping
    fun list(
        @Validated request: AdminPageRequest
    ): Response<AdminPageData<Report>> =
        success(facade.list(request.page, request.size, request.includeDeleted))

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long
    ): Response<Report> = success(facade.get(id))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Validated @RequestBody request: AdminReportUpdateRequest,
        @AuthenticationPrincipal user: UserCache,
    ): Response<Report> = success(facade.update(id, user.uid, request))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): Response<Unit> {
        facade.delete(id)
        return success()
    }
}
