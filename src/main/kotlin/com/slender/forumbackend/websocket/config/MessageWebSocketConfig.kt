package com.slender.forumbackend.websocket.config

import com.slender.forumbackend.websocket.auth.MessageWebSocketHandshakeInterceptor
import com.slender.forumbackend.websocket.handler.MessageWebSocketHandler
import jakarta.websocket.server.ServerContainer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class MessageWebSocketConfig(
    private val handler: MessageWebSocketHandler,
    private val handshakeInterceptor: MessageWebSocketHandshakeInterceptor,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, "/ws/message")
            .addInterceptors(handshakeInterceptor)
            .setAllowedOriginPatterns("*")
    }

    @Bean
    fun messageWebSocketContainerCustomizer(): ServletContextInitializer = ServletContextInitializer { servletContext ->
        val container = servletContext.getAttribute(ServerContainer::class.java.name) as? ServerContainer ?: return@ServletContextInitializer
        container.defaultMaxTextMessageBufferSize = MAX_TEXT_MESSAGE_BYTES
        container.defaultMaxSessionIdleTimeout = IDLE_TIMEOUT_MS
        container.setAsyncSendTimeout(SEND_TIMEOUT_MS)
    }

    private companion object {
        const val MAX_TEXT_MESSAGE_BYTES = 64 * 1024
        const val IDLE_TIMEOUT_MS = 30 * 60 * 1000L
        const val SEND_TIMEOUT_MS = 10_000L
    }
}
