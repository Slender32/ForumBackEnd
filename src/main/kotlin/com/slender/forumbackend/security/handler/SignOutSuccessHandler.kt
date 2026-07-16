package com.slender.forumbackend.security.handler

import com.slender.forumbackend.constant.core.Message.User.LOGOUT_SUCCESS
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

@Component
class SignOutSuccessHandler(
    private val writer: Writer
) : LogoutSuccessHandler {
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        writer.write<Unit>(success(LOGOUT_SUCCESS), response)
    }
}
