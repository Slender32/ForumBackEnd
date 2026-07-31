package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.enumeration.error.Error.UNAUTHENTICATED
import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
        writer.write(ExceptionAdvice(UNAUTHENTICATED), response)
    }
}
