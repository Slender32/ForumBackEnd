package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminSensitiveWordFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.governance.SensitiveWordData
import com.slender.forumbackend.model.request.AdminSensitiveWordListRequest
import com.slender.forumbackend.model.request.SensitiveWordRequest
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
@RequestMapping("/admin/sensitive-words")
@PreAuthorize("hasAuthority('sensitive-word:manage')")
@Tag(name = "Admin Sensitive Words", description = "Sensitive word dictionary management")
class AdminSensitiveWordController(
    private val facade: AdminSensitiveWordFacade,
) {
    @GetMapping
    @Operation(summary = "分页查询敏感词", description = "需要登录并具有 sensitive-word:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminSensitiveWordListRequest,
    ): Response<AdminPageData<SensitiveWordData>> =
        success(facade.list(request.page, request.size, request.includeDeleted, request.keyword, request.enabled))

    @PostMapping
    @Operation(summary = "新增敏感词", description = "需要登录并具有 sensitive-word:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun add(
        @Validated @RequestBody r: SensitiveWordRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.add(r, u.uid))

    @PutMapping("/{id}")
    @Operation(summary = "修改或启停敏感词", description = "需要登录并具有 sensitive-word:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun update(
        @Parameter(description = "敏感词 ID")
        @PathVariable id: Long,
        @Validated @RequestBody r: SensitiveWordRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache,
    ): Response<Int> = success(facade.update(id, r, u.uid))

    @DeleteMapping("/{id}")
    @Operation(summary = "软删除敏感词", description = "需要登录并具有 sensitive-word:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "敏感词 ID")
        @PathVariable id: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal u: UserCache
    ): Response<Int> =
        success(facade.delete(id, u.uid))
}
