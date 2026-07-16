package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.core.Message.Exception.UNKNOWN_ERROR
import com.slender.forumbackend.model.data.Response.Companion.fail
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class ExceptionHandler(
    private val writer: Writer
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        writer.write<Unit>(fail(UNAUTHORIZED.value(), UNKNOWN_ERROR), response)
    }
}
