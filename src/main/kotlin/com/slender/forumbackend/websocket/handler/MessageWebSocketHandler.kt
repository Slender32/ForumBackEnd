package com.slender.forumbackend.websocket.handler

import com.slender.forumbackend.toolkit.Json
import com.slender.forumbackend.websocket.auth.MessageWebSocketHandshakeInterceptor.Companion.PRINCIPAL_ATTRIBUTE
import com.slender.forumbackend.websocket.auth.WebSocketPrincipal
import com.slender.forumbackend.websocket.model.WsClientMessage
import com.slender.forumbackend.websocket.model.WsEnvelope
import com.slender.forumbackend.websocket.model.WsErrorData
import com.slender.forumbackend.websocket.model.WsHelloData
import com.slender.forumbackend.websocket.model.WsMessageType
import com.slender.forumbackend.websocket.service.MessageSocketSessionRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.lang.System.currentTimeMillis
import java.util.UUID

@Component
class MessageWebSocketHandler(
    private val json: Json,
    private val sessionRegistry: MessageSocketSessionRegistry,
) : TextWebSocketHandler() {
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val principal = session.attributes[PRINCIPAL_ATTRIBUTE] as? WebSocketPrincipal ?: run {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }
        sessionRegistry.register(principal.userId, session)
        sessionRegistry.send(session, envelope(WsMessageType.HELLO, WsHelloData(principal.userId)))
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionRegistry.unregister(session.id)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        sessionRegistry.unregister(session.id)
        if (session.isOpen) session.close(CloseStatus.SERVER_ERROR)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val clientMessage = runCatching { json.parse(message.payload, WsClientMessage::class) }.getOrNull()
        if (clientMessage?.type in setOf("PING", "PONG")) return
        sessionRegistry.send(
            session,
            envelope(
                WsMessageType.ERROR,
                WsErrorData("UNSUPPORTED_CLIENT_MESSAGE", "客户端消息不受支持"),
            ),
        )
    }

    private fun <T> envelope(type: WsMessageType, payload: T): WsEnvelope<T> =
        WsEnvelope(type, payload, UUID.randomUUID().toString(), currentTimeMillis())
}
