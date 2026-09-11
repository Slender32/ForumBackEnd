package com.slender.forumbackend.service.admin

import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.entity.user.rbac.Role
import com.slender.forumbackend.repository.RbacRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime.now

@Service
class AdminUserRoleService(
    private val users: UserReadRepository,
    private val rbac: RbacRepository,
    private val redis: StringRedisTemplate,
) {
    fun list(userId: Long): List<Role> {
        users.findByIdOrThrow(userId)
        return rbac.findEnabledRolesByUserId(userId)
    }

    @Transactional
    fun replace(userId: Long, roleIds: Set<Long>) {
        users.findByIdOrThrow(userId)
        val desired = rbac.findEnabledRolesByIds(roleIds)
        if (desired.map { it.roleId }.toSet() != roleIds) {
            throw InvalidRequestException("包含不存在或已禁用的角色")
        }
        val now = now()
        rbac.markRolesDeletedExcept(userId, roleIds, now)
        desired.forEach { rbac.bindRole(userId, it.roleId, now) }
        evictUserToken(userId)
    }

    @Transactional
    fun delete(userId: Long, roleId: Long) {
        users.findByIdOrThrow(userId)
        rbac.markRoleDeleted(userId, roleId, now())
        evictUserToken(userId)
    }

    private fun evictUserToken(userId: Long) {
        redis.delete(USER_LOGIN_CACHE + userId)
    }
}
