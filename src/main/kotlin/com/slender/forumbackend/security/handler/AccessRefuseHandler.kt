package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.enumeration.error.Error.AUTHORITY_INSUFFICIENT
import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class AccessRefuseHandler(
    private val writer: Writer
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        writer.write(ExceptionAdvice(AUTHORITY_INSUFFICIENT), response)
    }
}
