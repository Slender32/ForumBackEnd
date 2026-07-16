package com.slender.forumbackend.model.token

import org.springframework.security.authentication.AbstractAuthenticationToken

class RefreshToken(
    private val uid: Long
) : AbstractAuthenticationToken(null) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials() = null

    override fun getPrincipal() = uid
}
