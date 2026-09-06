package com.slender.forumbackend.websocket.auth

import java.security.Principal

data class WebSocketPrincipal(
    val userId: Long,
) : Principal {
    override fun getName(): String = userId.toString()
}
