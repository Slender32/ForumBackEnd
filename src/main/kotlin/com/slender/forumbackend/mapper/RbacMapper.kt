package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.constant.field.PermissionField.ENABLED as PERMISSION_ENABLED
import com.slender.forumbackend.constant.field.PermissionField.PERMISSION_ID
import com.slender.forumbackend.constant.field.RoleField.CODE
import com.slender.forumbackend.constant.field.RoleField.ENABLED as ROLE_ENABLED
import com.slender.forumbackend.constant.field.RoleField.ROLE_ID
import com.slender.forumbackend.constant.field.UserRoleField.USER_ID
import com.slender.forumbackend.model.entity.user.rbac.Permission
import com.slender.forumbackend.model.entity.user.rbac.Role
import com.slender.forumbackend.model.entity.user.rbac.RolePermission
import com.slender.forumbackend.model.entity.user.relation.UserRole
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Mapper
interface RoleMapper : BaseMapper<Role>

@Mapper
interface PermissionMapper : BaseMapper<Permission>

@Mapper
interface RolePermissionMapper : BaseMapper<RolePermission>

@Mapper
interface UserRoleMapper : BaseMapper<UserRole>

@Repository
class RbacRepository(
    private val roleMapper: RoleMapper,
    private val permissionMapper: PermissionMapper,
    private val rolePermissionMapper: RolePermissionMapper,
    private val userRoleMapper: UserRoleMapper,
) {
    fun findEnabledRolesByUserId(uid: Long): List<Role> {
        val roleIds = userRoleMapper.selectList(
            QueryWrapper<UserRole>().eq(USER_ID, uid)
        ).map { it.roleId }

        if (roleIds.isEmpty()) return emptyList()

        return roleMapper.selectList(
            QueryWrapper<Role>()
                .`in`(ROLE_ID, roleIds)
                .eq(ROLE_ENABLED, true)
        )
    }

    fun findAuthoritiesByUserId(uid: Long): Set<String> {
        val roles = findEnabledRolesByUserId(uid)

        val permissions = if (roles.isEmpty()) {
            emptyList()
        } else {
            val roleIds = roles.map { it.roleId }
            val permissionIds = rolePermissionMapper.selectList(
                QueryWrapper<RolePermission>().`in`(ROLE_ID, roleIds)
            ).map { it.permissionId }

            if (permissionIds.isEmpty()) emptyList()
            else permissionMapper.selectList(
                QueryWrapper<Permission>()
                    .`in`(PERMISSION_ID, permissionIds)
                    .eq(PERMISSION_ENABLED, true)
            )
        }

        return (roles.map { it.authority } + permissions.map { it.code }).toSet()
    }

    fun findEnabledRoleIdByCode(code: String): Long? =
        roleMapper.selectOne(
            QueryWrapper<Role>()
                .eq(CODE, code)
                .eq(ROLE_ENABLED, true)
        )?.roleId

    fun bindRole(uid: Long, roleId: Long, createTime: LocalDateTime) {
        userRoleMapper.insert(UserRole(uid, roleId, createTime))
    }
}
