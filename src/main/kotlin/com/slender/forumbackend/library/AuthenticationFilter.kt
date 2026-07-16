package com.slender.forumbackend.library

import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

abstract class AuthenticationFilter(
    url: String,
    authenticationSuccessHandler: AuthenticationSuccessHandler,
    authenticationFailureHandler: AuthenticationFailureHandler
): AbstractAuthenticationProcessingFilter(url) {
    protected val log = logger()

    init {
        authenticationManager = { it }
        setAuthenticationSuccessHandler(authenticationSuccessHandler)
        setAuthenticationFailureHandler(authenticationFailureHandler)
    }
}