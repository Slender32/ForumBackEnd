package com.slender.forumbackend.model.token

import com.slender.forumbackend.model.cache.UserCache
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AuthenticatedToken(
    private val userCache: UserCache
) : AbstractAuthenticationToken (
    userCache.authorities.map(::SimpleGrantedAuthority)
) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials() = null

    override fun getPrincipal() = userCache
}
