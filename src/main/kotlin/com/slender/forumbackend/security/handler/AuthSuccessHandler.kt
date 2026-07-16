package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.core.Message.User.LOGIN_SUCCESS
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.Redis.Time.ACCESS_TOKEN_EXPIRE_TIME
import com.slender.forumbackend.constant.util.JwtToolkit.accessToken
import com.slender.forumbackend.constant.util.JwtToolkit.refreshToken
import com.slender.forumbackend.library.logger
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.model.data.LoginData
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.toolkit.Json
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class AuthSuccessHandler(
    private val writer: Writer,
    private val redisTemplate: StringRedisTemplate,
    private val json: Json,
) : AuthenticationSuccessHandler {
    private companion object {
        val log = logger()
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = authentication.principal as User
        val authorities = authentication.authorities.mapNotNull { it.authority }.toSet()

        user.apply {
            val loginDataCache = LoginDataCache(uid, name, email, avatar, authorities)
            redisTemplate.opsForValue().set(
                USER_LOGIN_CACHE + uid,
                json.format(loginDataCache),
                ACCESS_TOKEN_EXPIRE_TIME,
            )
            log.info("登录成功 {}", uid)
            writer.write(
                success(
                    LOGIN_SUCCESS,
                    LoginData(
                        uid,
                        name,
                        accessToken(uid),
                        refreshToken(uid),
                        user.toUserData(),
                    )
                ),
                response,
            )
        }
    }
}
