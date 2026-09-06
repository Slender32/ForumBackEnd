package com.slender.forumbackend.websocket.auth

import com.slender.forumbackend.constant.core.Jwt.ACCESS_KEY
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.field.UserField.UID
import com.slender.forumbackend.constant.util.JwtToolkit.parseToken
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class MessageWebSocketTokenService(
    private val json: Json,
    private val redisTemplate: StringRedisTemplate,
) {
    fun authenticate(accessToken: String): WebSocketPrincipal? = runCatching {
        val uid = parseToken(ACCESS_KEY, accessToken)[UID]?.toString()?.toLongOrNull()
            ?: return null
        val cachedLogin = redisTemplate.opsForValue().get(USER_LOGIN_CACHE + uid) ?: return null
        val login = json.parse(cachedLogin, LoginDataCache::class)
        if (login.uid != uid) return null
        WebSocketPrincipal(uid)
    }.getOrNull()
}
