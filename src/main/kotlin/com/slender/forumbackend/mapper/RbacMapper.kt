package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.user.rbac.Permission
import com.slender.forumbackend.model.entity.user.rbac.Role
import com.slender.forumbackend.model.entity.user.rbac.RolePermission
import com.slender.forumbackend.model.entity.user.relation.UserRole
import org.apache.ibatis.annotations.Mapper

@Mapper
interface RoleMapper : BaseMapper<Role>

@Mapper
interface PermissionMapper : BaseMapper<Permission>

@Mapper
interface RolePermissionMapper : BaseMapper<RolePermission>

@Mapper
interface UserRoleMapper : BaseMapper<UserRole>
