package com.slender.forumbackend.websocket.service

import com.slender.forumbackend.toolkit.Json
import com.slender.forumbackend.websocket.model.WsEnvelope
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import java.util.concurrent.ConcurrentHashMap

@Service
class MessageSocketSessionRegistry(
    private val json: Json,
) {
    private val sessionsByUser = ConcurrentHashMap<Long, ConcurrentHashMap<String, WebSocketSession>>()
    private val usersBySession = ConcurrentHashMap<String, Long>()

    fun register(userId: Long, session: WebSocketSession) {
        val concurrentSession = ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT_MS, SEND_BUFFER_LIMIT_BYTES)
        sessionsByUser.computeIfAbsent(userId) { ConcurrentHashMap() }[session.id] = concurrentSession
        usersBySession[session.id] = userId
    }

    fun unregister(sessionId: String) {
        val userId = usersBySession.remove(sessionId) ?: return
        sessionsByUser[userId]?.let { sessions ->
            sessions.remove(sessionId)
            if (sessions.isEmpty()) sessionsByUser.remove(userId, sessions)
        }
    }

    fun send(userId: Long, envelope: WsEnvelope<*>) {
        val payload = runCatching { json.format(envelope) }.getOrNull() ?: return
        sessionsByUser[userId]?.forEach { (sessionId, session) ->
            if (!send(session, payload)) unregister(sessionId)
        }
    }

    fun send(session: WebSocketSession, envelope: WsEnvelope<*>) {
        val payload = runCatching { json.format(envelope) }.getOrNull() ?: return
        val registeredSession = usersBySession[session.id]
            ?.let { userId -> sessionsByUser[userId]?.get(session.id) }
            ?: session
        if (!send(registeredSession, payload)) unregister(session.id)
    }

    private fun send(session: WebSocketSession, payload: String): Boolean = runCatching {
        if (!session.isOpen) return false
        session.sendMessage(TextMessage(payload))
        true
    }.getOrDefault(false)

    private companion object {
        const val SEND_TIME_LIMIT_MS = 10_000
        const val SEND_BUFFER_LIMIT_BYTES = 64 * 1024
    }
}
