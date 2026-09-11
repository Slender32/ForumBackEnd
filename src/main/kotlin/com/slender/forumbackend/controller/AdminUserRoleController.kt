package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.user.rbac.Role
import com.slender.forumbackend.model.request.UserRoleUpdateRequest
import com.slender.forumbackend.facade.AdminUserRoleFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated

@RestController
@RequestMapping("/admin/users/{uid}/roles")
@PreAuthorize("hasAuthority('role:manage')")
class AdminUserRoleController(
    private val facade: AdminUserRoleFacade,
) {
    @GetMapping
    fun list(
        @PathVariable uid: Long
    ): Response<List<Role>> = success(facade.list(uid))

    @PutMapping
    fun replace(
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
    fun delete(
        @PathVariable uid: Long,
        @PathVariable roleId: Long
    ): Response<Unit> {
        facade.delete(uid, roleId)
        return success()
    }
}
