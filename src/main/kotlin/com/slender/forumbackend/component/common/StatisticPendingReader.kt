package com.slender.forumbackend.component.common

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class StatisticPendingReader(
    private val redisTemplate: StringRedisTemplate,
) {
    fun pendingEntries(key: String): Map<String, String> =
        redisTemplate.opsForHash<String, String>().entries(key)
            .mapKeys { it.key }
            .mapValues { it.value.toString() }

    companion object {
        const val PENDING_TRUE = "1"
        const val PENDING_FALSE = "0"

        fun String.toPendingTarget(): PendingTarget? {
            val parts = split(":", limit = 2)
            if (parts.size != 2) return null
            val targetId = parts[0].toLongOrNull() ?: return null
            val userId = parts[1].toLongOrNull() ?: return null
            return PendingTarget(targetId, userId)
        }

        fun String.toPendingBoolean(): Boolean? = when (this) {
            PENDING_TRUE -> true
            PENDING_FALSE -> false
            else -> null
        }
    }
}

data class PendingTarget(
    val targetId: Long,
    val userId: Long,
)