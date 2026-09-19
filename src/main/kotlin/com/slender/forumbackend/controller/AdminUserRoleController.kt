package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminUserRoleFacade
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.user.rbac.Role
import com.slender.forumbackend.model.request.UserRoleUpdateRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "AdminUserRole", description = "管理端用户角色管理")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/users/{uid}/roles")
@PreAuthorize("hasAuthority('role:manage')")
class AdminUserRoleController(
    private val facade: AdminUserRoleFacade,
) {
    @GetMapping
    @Operation(summary = "查询用户角色", description = "需要登录并具有 role:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @Parameter(description = "用户 ID")
        @PathVariable uid: Long
    ): Response<List<Role>> = success(facade.list(uid))

    @PutMapping
    @Operation(summary = "替换用户角色", description = "需要登录并具有 role:manage 权限。提交完整角色集合，空集合将清空用户角色。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun replace(
        @Parameter(description = "用户 ID")
        @PathVariable
        uid: Long,

        @Validated
        @RequestBody
        request: UserRoleUpdateRequest,
    ): Response<Unit> {
        facade.replace(uid, request.roleIds)
        return success()
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除用户角色", description = "需要登录并具有 role:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "用户 ID")
        @PathVariable uid: Long,
        @Parameter(description = "角色 ID")
        @PathVariable roleId: Long
    ): Response<Unit> {
        facade.delete(uid, roleId)
        return success()
    }
}
