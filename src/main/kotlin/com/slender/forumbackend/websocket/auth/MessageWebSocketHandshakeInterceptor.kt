package com.slender.forumbackend.websocket.auth

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder

@Component
class MessageWebSocketHandshakeInterceptor(
    private val tokenService: MessageWebSocketTokenService,
) : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        val token = request.headers.getFirst(AUTHORIZATION).bearerToken()
            ?: request.queryParameters().getFirst("access_token")
        val principal = token?.let(tokenService::authenticate) ?: run {
            response.setStatusCode(UNAUTHORIZED)
            return false
        }
        attributes[PRINCIPAL_ATTRIBUTE] = principal
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) = Unit

    private fun String?.bearerToken(): String? = when {
        isNullOrBlank() -> null
        startsWith(BEARER_PREFIX) -> substring(BEARER_PREFIX.length).takeIf(String::isNotBlank)
        else -> ""
    }

    private fun ServerHttpRequest.queryParameters(): MultiValueMap<String, String> =
        UriComponentsBuilder.fromUri(uri).build().queryParams

    companion object {
        const val PRINCIPAL_ATTRIBUTE = "messageWebSocketPrincipal"
        private const val BEARER_PREFIX = "Bearer "
    }
}
