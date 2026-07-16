package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.core.Message.Exception.BLOCK_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.EMAIL_OR_PASSWORD_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.HAS_LOGIN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.LOGIN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REQUEST_CONTENT_ERROR
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.HasLoginException
import com.slender.forumbackend.exception.LoginMisMatchException
import com.slender.forumbackend.exception.RequestContentException
import com.slender.forumbackend.exception.ValidationException
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.fail
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
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
        val responseData = exception.toErrorResponse()
        writer.write(responseData, response)
    }

    private fun AuthenticationException.toErrorResponse(): Response<Unit> = when (this) {
        is ValidationException,
        is LoginMisMatchException -> fail(BAD_REQUEST.value(), message ?: EMAIL_OR_PASSWORD_ERROR)
        is HasLoginException -> fail(BAD_REQUEST.value(), HAS_LOGIN_ERROR)
        is RequestContentException -> fail(BAD_REQUEST.value(), REQUEST_CONTENT_ERROR)
        is BlockException -> fail(FORBIDDEN.value(), BLOCK_ERROR)
        else -> fail(LOGIN_ERROR)
    }
}
