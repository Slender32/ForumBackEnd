package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.core.Message.Exception.AUTHORITY_ERROR
import com.slender.forumbackend.model.data.Response.Companion.fail
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.FORBIDDEN
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
        writer.write<Unit>(fail(FORBIDDEN.value(), AUTHORITY_ERROR), response)
    }
}
