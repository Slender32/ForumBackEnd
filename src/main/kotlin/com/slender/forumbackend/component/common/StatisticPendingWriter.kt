package com.slender.forumbackend.component.common

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class StatisticPendingWriter(
    private val redisTemplate: StringRedisTemplate,
) {
    @Async
    fun putPending(key: String, targetId: Long, userId: Long, value: String) {
        redisTemplate.opsForHash<String, String>()
            .put(key, "$targetId:$userId", value)
    }

    @Async
    fun toggleLike(key: String, targetId: Long, userId: Long, databaseLiked: Boolean) {
        redisTemplate.execute(
            TOGGLE_PENDING,
            listOf(key),
            "$targetId:$userId",
            if (databaseLiked) "1" else "0",
        )
    }

    fun deletePendingValueIfUnchanged(key: String, field: String, expectedValue: String): Long? =
        redisTemplate.execute(DELETE_PENDING_IF_UNCHANGED, listOf(key), field, expectedValue)

    private companion object {
        val DELETE_PENDING_IF_UNCHANGED = DefaultRedisScript(
            """
            if redis.call('HGET', KEYS[1], ARGV[1]) == ARGV[2] then
                return redis.call('HDEL', KEYS[1], ARGV[1])
            end
            return 0
            """.trimIndent(),
            Long::class.java,
        )

        val TOGGLE_PENDING = DefaultRedisScript(
            """
            local current = redis.call('HGET', KEYS[1], ARGV[1])
            if current ~= '1' and current ~= '0' then
                current = ARGV[2]
            end
            local next = '1'
            if current == '1' then
                next = '0'
            end
            redis.call('HSET', KEYS[1], ARGV[1], next)
            return next
            """.trimIndent(),
            String::class.java,
        )
    }
}
