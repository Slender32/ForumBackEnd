package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.Redis.Time.ACCESS_TOKEN_EXPIRE_TIME
import com.slender.forumbackend.constant.util.JwtToolkit.accessToken
import com.slender.forumbackend.constant.util.JwtToolkit.refreshToken
import com.slender.forumbackend.exception.UserNotFoundException
import com.slender.forumbackend.mapper.RbacRepository
import com.slender.forumbackend.mapper.UserMapper
import com.slender.forumbackend.mapper.UserRepository
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.model.data.RefreshData
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.token.RefreshToken
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

interface UserService : IService<User> {
    fun refresh(uid: Long): RefreshData
}

@Service
class UserServiceImpl(
    private val json: Json,
    private val redisTemplate: StringRedisTemplate,
    private val userRepository: UserRepository,
    private val rbacRepository: RbacRepository,
) : UserService, ServiceImpl<UserMapper, User>() {
    override fun refresh(uid: Long): RefreshData {
        val authorities = rbacRepository.findAuthoritiesByUserId(uid)
        val user = getById(uid) ?: throw UserNotFoundException()
        val loginDataCache = LoginDataCache(
            uid,user.name, user.email, user.avatar, authorities
        )
        redisTemplate.opsForValue().set(
            USER_LOGIN_CACHE + uid,
            json.format(loginDataCache),
            ACCESS_TOKEN_EXPIRE_TIME,
        )
        return RefreshData(uid, user.name, accessToken(uid), refreshToken(uid))
    }
}
