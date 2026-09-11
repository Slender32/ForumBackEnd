package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.PermissionField.ENABLED as PERMISSION_ENABLED
import com.slender.forumbackend.constant.field.PermissionField.PERMISSION_ID
import com.slender.forumbackend.constant.field.RoleField.CODE
import com.slender.forumbackend.constant.field.RoleField.ENABLED as ROLE_ENABLED
import com.slender.forumbackend.constant.field.RoleField.ROLE_ID
import com.slender.forumbackend.constant.field.UserRoleField.USER_ID
import com.slender.forumbackend.mapper.PermissionMapper
import com.slender.forumbackend.mapper.RoleMapper
import com.slender.forumbackend.mapper.RolePermissionMapper
import com.slender.forumbackend.mapper.UserRoleMapper
import com.slender.forumbackend.model.entity.user.rbac.Permission
import com.slender.forumbackend.model.entity.user.rbac.Role
import com.slender.forumbackend.model.entity.user.rbac.RolePermission
import com.slender.forumbackend.model.entity.user.relation.UserRole
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class RbacRepository(
    private val roleMapper: RoleMapper,
    private val permissionMapper: PermissionMapper,
    private val rolePermissionMapper: RolePermissionMapper,
    private val userRoleMapper: UserRoleMapper,
) : ServiceImpl<RoleMapper, Role>(), IService<Role> {
    fun findEnabledRolesByUserId(uid: Long): List<Role> {
        val roleIds =
            userRoleMapper
                .selectList(QueryWrapper<UserRole>().eq(USER_ID, uid).isNull("deleted_at"))
                .map { it.roleId }

        if (roleIds.isEmpty()) return emptyList()

        return roleMapper.selectList(
            QueryWrapper<Role>().`in`(ROLE_ID, roleIds).eq(ROLE_ENABLED, true)
        )
    }

    fun findEnabledRolesByIds(roleIds: Collection<Long>): List<Role> =
        roleIds
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.let {
                roleMapper.selectList(QueryWrapper<Role>().`in`(ROLE_ID, it).eq(ROLE_ENABLED, true))
            } ?: emptyList()

    fun findAuthoritiesByUserId(uid: Long): Set<String> {
        val roles = findEnabledRolesByUserId(uid)

        val permissions =
            if (roles.isEmpty()) {
                emptyList()
            } else {
                val roleIds = roles.map { it.roleId }
                val permissionIds =
                    rolePermissionMapper
                        .selectList(QueryWrapper<RolePermission>().`in`(ROLE_ID, roleIds))
                        .map { it.permissionId }

                if (permissionIds.isEmpty()) emptyList()
                else
                    permissionMapper.selectList(
                        QueryWrapper<Permission>()
                            .`in`(PERMISSION_ID, permissionIds)
                            .eq(PERMISSION_ENABLED, true)
                    )
            }

        return (roles.map { it.authority } + permissions.map { it.code }).toSet()
    }

    fun findEnabledRoleIdByCode(code: String): Long? =
        roleMapper.selectOne(QueryWrapper<Role>().eq(CODE, code).eq(ROLE_ENABLED, true))?.roleId

    fun bindRole(uid: Long, roleId: Long, createTime: LocalDateTime) {
        val active =
            userRoleMapper.selectCount(
                QueryWrapper<UserRole>().eq(USER_ID, uid).eq("role_id", roleId).isNull("deleted_at")
            ) > 0
        if (active) return
        val restored =
            userRoleMapper.update(
                null,
                UpdateWrapper<UserRole>()
                    .eq(USER_ID, uid)
                    .eq("role_id", roleId)
                    .isNotNull("deleted_at")
                    .set("deleted_at", null)
                    .set("create_time", createTime),
            )
        if (restored > 0) return
        userRoleMapper.insert(UserRole(uid, roleId, createTime))
    }

    fun markRoleDeleted(uid: Long, roleId: Long, now: LocalDateTime): Boolean =
        userRoleMapper.update(
            null,
            UpdateWrapper<UserRole>()
                .eq(USER_ID, uid)
                .eq("role_id", roleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        ) > 0

    fun markRolesDeletedExcept(uid: Long, retainedRoleIds: Collection<Long>, now: LocalDateTime) {
        userRoleMapper.update(null, UpdateWrapper<UserRole>()
            .eq(USER_ID, uid)
            .isNull("deleted_at")
            .set("deleted_at", now)
            .notIn("role_id", if(retainedRoleIds.isEmpty()) listOf(-1) else retainedRoleIds.distinct())
        )
    }
}
