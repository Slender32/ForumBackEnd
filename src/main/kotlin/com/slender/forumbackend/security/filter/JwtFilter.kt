package com.slender.forumbackend.security.filter

import com.slender.forumbackend.constant.core.Jwt.ACCESS_KEY
import com.slender.forumbackend.constant.core.Jwt.REFRESH_KEY
import com.slender.forumbackend.constant.core.Message.Exception.BLOCK_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.INTERNAL_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.LOGIN_EXPIRED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.LOGIN_NOT_EXPIRED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_EXPIRE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_NOT_FOUND
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_SIGNATURE_ERROR
import com.slender.forumbackend.constant.core.Redis.Key.USER_BLOCK
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.URL.REFRESH
import com.slender.forumbackend.constant.field.UserField.UID
import com.slender.forumbackend.constant.util.JwtToolkit.parseToken
import com.slender.forumbackend.exception.*
import com.slender.forumbackend.library.RequireToken.requireToken
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.model.data.Response.Companion.exception
import com.slender.forumbackend.model.token.AuthenticatedToken
import com.slender.forumbackend.model.token.RefreshToken
import com.slender.forumbackend.toolkit.Json
import com.slender.forumbackend.toolkit.Writer
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus.*
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

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val isRefreshRequest = pathMatcher.match(REFRESH, request.requestURI)
        if (!requireToken(request.requestURI, pathMatcher) && !isRefreshRequest) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val token = request.bearerToken()
            getContext().authentication = if (isRefreshRequest) {
                val uid = token.parseUid(REFRESH_KEY)
                with(redisTemplate.opsForValue()) {
                    if (get(USER_BLOCK + uid) != null) throw BlockException()
                    if (get(USER_LOGIN_CACHE + uid) != null) throw LoginNotExpiredException()
                }
                RefreshToken(uid.toLong())
            } else {
                val uid = token.parseUid(ACCESS_KEY)
                val cacheData = redisTemplate.opsForValue().run {
                    get(USER_LOGIN_CACHE + uid) ?: throw LoginExpiredException()
                }
                val loginCache = json.parse(cacheData, LoginDataCache::class)
                AuthenticatedToken(loginCache.toUserCache())
            }
        }.onFailure {
            val responseData = when (it) {
                is LoginException -> when (it) {
                    is TokenNotFoundException -> exception(UNAUTHORIZED.value(), TOKEN_NOT_FOUND)
                    is BlockException -> exception(FORBIDDEN.value(), BLOCK_ERROR)
                    is LoginNotExpiredException -> exception(BAD_REQUEST.value(), LOGIN_NOT_EXPIRED_ERROR)
                    is LoginExpiredException -> exception(UNAUTHORIZED.value(), LOGIN_EXPIRED_ERROR)
                    else -> exception(INTERNAL_SERVER_ERROR.value(), INTERNAL_ERROR)
                }
                is SignatureException -> exception(BAD_REQUEST.value(), TOKEN_SIGNATURE_ERROR)
                is ExpiredJwtException -> exception(UNAUTHORIZED.value(), TOKEN_EXPIRE_ERROR)
                is JwtException -> exception(BAD_REQUEST.value(), TOKEN_SIGNATURE_ERROR)
                else -> exception(INTERNAL_SERVER_ERROR.value(), INTERNAL_ERROR)
            }
            writer.write(responseData, response)
        }.onSuccess {
            filterChain.doFilter(request, response)
        }
    }

    private final fun HttpServletRequest.bearerToken(): String {
        val header = getHeader("Authorization")
        if (header.isNullOrBlank() || !header.startsWith("Bearer "))
            throw TokenNotFoundException()
        else return header.substring(7)
    }

    private final fun String.parseUid(key: String): String {
        val payload = parseToken(key, this)
        val uid = payload.getOrDefault(UID, "").toString()
        if(uid.isBlank()) throw TokenNotFoundException()
        return uid
    }
}
