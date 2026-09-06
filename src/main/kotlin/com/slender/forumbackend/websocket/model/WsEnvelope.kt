package com.slender.forumbackend.websocket.model

data class WsEnvelope<T>(
    val type: WsMessageType,
    val payload: T,
    val traceId: String,
    val serverTime: Long,
)

enum class WsMessageType {
    HELLO,
    CHAT_MESSAGE_SENT,
    CONVERSATION_UPDATED,
    CONVERSATION_READ,
    ERROR,
}

data class WsHelloData(
    val userId: Long,
)

data class WsErrorData(
    val code: String,
    val message: String,
    val relatedTraceId: String? = null,
)

data class WsClientMessage(
    val type: String? = null,
)
