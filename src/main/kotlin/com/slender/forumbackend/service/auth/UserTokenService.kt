package com.slender.forumbackend.service.auth

import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.Redis.Time.ACCESS_TOKEN_EXPIRE_TIME
import com.slender.forumbackend.constant.util.JwtToolkit.accessToken
import com.slender.forumbackend.constant.util.JwtToolkit.refreshToken
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.model.data.RefreshData
import com.slender.forumbackend.repository.RbacRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class UserTokenService(
    private val json: Json,
    private val redisTemplate: StringRedisTemplate,
    private val userReadRepository: UserReadRepository,
    private val rbacRepository: RbacRepository,
) {
    fun refresh(uid: Long): RefreshData {
        val user = userReadRepository.findActiveByIdOrThrow(uid)
        val authorities = rbacRepository.findAuthoritiesByUserId(uid)
        val loginDataCache = user.run { LoginDataCache(uid, name, email, avatar, authorities) }
        redisTemplate.opsForValue().set(
            USER_LOGIN_CACHE + uid,
            json.format(loginDataCache),
            ACCESS_TOKEN_EXPIRE_TIME,
        )
        return RefreshData(
            accessToken(uid),
            refreshToken(uid),
            userData = user.toUserData(userReadRepository.findStatisticsById(uid)),
        )
    }
}
