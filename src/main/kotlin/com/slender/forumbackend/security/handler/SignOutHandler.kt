package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.model.cache.UserCache
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component

@Component
class SignOutHandler(
    private val redisTemplate: StringRedisTemplate
) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        if (authentication == null) return
        val uid = (authentication.principal as UserCache).uid
        redisTemplate.delete(USER_LOGIN_CACHE + uid)
    }
}
