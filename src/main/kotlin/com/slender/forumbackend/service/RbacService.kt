package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.RbacRepository
import com.slender.forumbackend.mapper.RoleMapper
import com.slender.forumbackend.model.entity.user.rbac.Role
import org.springframework.stereotype.Service

interface RbacService : IService<Role>

@Service
class RbacServiceImpl(
    private val rbacRepository: RbacRepository,
) : RbacService, ServiceImpl<RoleMapper, Role>()
