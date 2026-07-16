package com.slender.forumbackend.security.filter

import com.slender.forumbackend.constant.core.Message.Exception.EMAIL_OR_PASSWORD_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REQUEST_READ_ERROR
import com.slender.forumbackend.constant.core.Redis.Key.USER_BLOCK
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.URL.LOGIN
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.HasLoginException
import com.slender.forumbackend.exception.JsonParseException
import com.slender.forumbackend.exception.LoginMisMatchException
import com.slender.forumbackend.exception.RequestContentException
import com.slender.forumbackend.library.AuthenticationFilter
import com.slender.forumbackend.mapper.RbacRepository
import com.slender.forumbackend.mapper.UserRepository
import com.slender.forumbackend.constant.enumeration.user.UserStatus.ACTIVE
import com.slender.forumbackend.model.request.LoginRequest
import com.slender.forumbackend.model.token.LoginToken
import com.slender.forumbackend.security.handler.AuthFailureHandler
import com.slender.forumbackend.security.handler.AuthSuccessHandler
import com.slender.forumbackend.toolkit.Json
import com.slender.forumbackend.toolkit.Validator
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import tools.jackson.databind.exc.MismatchedInputException
import java.io.IOException

@Component
class PasswordFilter(
    private val json: Json,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val rbacRepository: RbacRepository,
    private val redisTemplate: StringRedisTemplate,
    private val validator: Validator,
    authSuccessHandler: AuthSuccessHandler,
    authFailureHandler: AuthFailureHandler,
) : AuthenticationFilter (
    LOGIN,
    authSuccessHandler,
    authFailureHandler
) {
    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication? = runCatching {
        val loginRequest = json.parse(request.inputStream, LoginRequest::class)
        validator.validate(loginRequest)

        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw LoginMisMatchException(EMAIL_OR_PASSWORD_ERROR)

        with(redisTemplate) {
            delete(USER_BLOCK + user.uid)
            val cache = opsForValue().get(USER_LOGIN_CACHE + user.uid)
            if(cache != null) throw HasLoginException()
        }

        if (user.status != ACTIVE) throw BlockException()

        val matched = passwordEncoder.matches(loginRequest.password, user.passwordHash)
        if (!matched) throw LoginMisMatchException(EMAIL_OR_PASSWORD_ERROR)

        LoginToken(user, rbacRepository.findAuthoritiesByUserId(user.uid))
    }.onFailure {
        when (it) {
            is JsonParseException, is NullPointerException, is MismatchedInputException -> throw RequestContentException()
            is IOException -> log.info(REQUEST_READ_ERROR, it) //TODO 接入错误日志处理平台
            else -> throw it
        }
    }.getOrNull()
}
