package com.slender.forumbackend.security.filter

import com.slender.forumbackend.constant.core.Jwt.ACCESS_KEY
import com.slender.forumbackend.constant.core.Jwt.REFRESH_KEY
import com.slender.forumbackend.constant.core.Redis.Key.USER_BLOCK
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.URL.REFRESH
import com.slender.forumbackend.constant.enumeration.error.Error.*
import com.slender.forumbackend.constant.field.UserField.UID
import com.slender.forumbackend.constant.util.JwtToolkit.parseToken
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.LoginExpiredException
import com.slender.forumbackend.exception.TokenNotFoundException
import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.library.RequireToken.requireToken
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.model.token.AuthenticatedToken
import com.slender.forumbackend.model.token.RefreshToken
import com.slender.forumbackend.toolkit.Json
import com.slender.forumbackend.toolkit.Writer
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.stereotype.Component
import org.springframework.util.PathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val json: Json,
    private val writer: Writer,
    private val pathMatcher: PathMatcher,
    private val redisTemplate: StringRedisTemplate,
) : OncePerRequestFilter() {

    private companion object {
        const val AUTHORIZATION = "Authorization"
        const val PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val isRefreshRequest = pathMatcher.match(REFRESH, request.requestURI)
        val requireToken = requireToken(request.requestURI, pathMatcher)
        if (!requireToken && !isRefreshRequest) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val token = request.bearerToken()
            val redis = redisTemplate.opsForValue()
            getContext().authentication = if (isRefreshRequest) {
                val uid = token.uid(REFRESH_KEY)
                val block = redis.get(USER_BLOCK + uid)
                if (block != null) throw BlockException()
                RefreshToken(uid.toLong())
            } else {
                val uid = token.uid(ACCESS_KEY)
                val cacheData = redis.get(USER_LOGIN_CACHE + uid) ?: throw LoginExpiredException()
                val loginCache = json.parse(cacheData, LoginDataCache::class)
                AuthenticatedToken(loginCache.toUserCache())
            }
        }.onFailure {
            val error = it.toError(isRefreshRequest)
            writer.write(ExceptionAdvice(error), response)
        }.onSuccess {
            filterChain.doFilter(request, response)
        }
    }

    private final fun Throwable.toError(isRefreshRequest: Boolean) = when (this) {
        is TokenNotFoundException -> TOKEN_MISSING
        is BlockException -> USER_BLOCKED
        is LoginExpiredException -> ACCESS_TOKEN_EXPIRED
        is ExpiredJwtException -> if (isRefreshRequest) REFRESH_TOKEN_EXPIRED else ACCESS_TOKEN_EXPIRED
        is JwtException -> TOKEN_INVALID
        else -> INTERNAL
    }

    private final fun HttpServletRequest.bearerToken() = getHeader(AUTHORIZATION)
        .apply { if (isNullOrBlank() || !startsWith(PREFIX)) throw TokenNotFoundException() }
        .substring(PREFIX.length)


    private final fun String.uid(key: String): String {
        val payload = parseToken(key, this)
        val uid = payload[UID] ?: throw TokenNotFoundException()
        return uid.toString()
    }
}
