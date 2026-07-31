package com.slender.forumbackend.model.token

import com.slender.forumbackend.model.data.UserData
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

class LoginToken(
    private val userData: UserData,
    authorities: Collection<String>
) : AbstractAuthenticationToken(authorities.map(::SimpleGrantedAuthority)) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials() = null

    override fun getPrincipal() = userData
}
