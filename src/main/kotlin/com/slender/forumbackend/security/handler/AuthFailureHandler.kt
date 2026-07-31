package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.enumeration.error.Error.*
import com.slender.forumbackend.exception.*
import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class AuthFailureHandler(
    private val writer: Writer
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val advice = exception.toError()
        writer.write(advice, response)
    }

    private final fun AuthenticationException.toError() = when (this) {
        is ValidationException -> ExceptionAdvice(REQUEST_CONTENT_INVALID, message)
        is LoginMisMatchException -> ExceptionAdvice(LOGIN_MISMATCH, message)
        is RequestContentException -> ExceptionAdvice(REQUEST_CONTENT_INVALID)
        is BlockException -> ExceptionAdvice(USER_BLOCKED)
        is TokenNotFoundException -> ExceptionAdvice(TOKEN_MISSING)
        is LoginExpiredException -> ExceptionAdvice(ACCESS_TOKEN_EXPIRED)
        else -> ExceptionAdvice(LOGIN_FAILED)
    }
}
