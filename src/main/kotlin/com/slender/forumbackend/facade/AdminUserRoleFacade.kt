package com.slender.forumbackend.facade

import com.slender.forumbackend.service.admin.AdminUserRoleService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserRoleFacade(
    private val service: AdminUserRoleService
) {
    fun list(userId: Long) = service.list(userId)

    @Transactional
    fun replace(userId: Long, roleIds: Set<Long>) = service.replace(userId, roleIds)

    @Transactional
    fun delete(userId: Long, roleId: Long) = service.delete(userId, roleId)
}
