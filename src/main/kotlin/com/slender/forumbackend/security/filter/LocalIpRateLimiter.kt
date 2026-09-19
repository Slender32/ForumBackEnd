package com.slender.forumbackend.security.filter

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.bucket4j.Bucket
import io.github.bucket4j.ConsumptionProbe
import io.github.bucket4j.TimeMeter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class LocalIpRateLimiter (
    private val properties: PublicRateLimitProperties
) {
    private final val timeMeter = object : TimeMeter {
        override fun currentTimeNanos(): Long = System.nanoTime()
        override fun isWallClockBased(): Boolean = false
    }

    private data class Group(
        val capacity: Long,
        val refillPerMinute: Long,
        val buckets: Cache<String, Bucket>,
    )

    private final val groups: Map<String, Group>

    init {
        properties.validate()
        groups = mapOf(
            "browsing" to properties.browsing,
            "search" to properties.search,
            "login" to properties.login,
            "captcha" to properties.captcha,
            "account" to properties.account,
        ).mapValues { (_, rule) ->
            val idleTimeout = Duration.ofMinutes(1).multipliedBy(rule.capacity)
                .dividedBy(rule.refillPerMinute).plusSeconds(1)
            Group(
                rule.capacity,
                rule.refillPerMinute,
                Caffeine.newBuilder()
                    .ticker { System.nanoTime() }
                    .expireAfterAccess(idleTimeout)
                    .build(),
            )
        }
    }

    fun consume(ip: String, group: String): ConsumptionProbe {
        val rule = groups.getValue(group)
        val bucket = rule.buckets.get(ip) {
            Bucket.builder()
                .withCustomTimePrecision(timeMeter)
                .addLimit { limit ->
                    limit.capacity(rule.capacity)
                        .refillGreedy(rule.refillPerMinute, Duration.ofMinutes(1))
                }
                .build()
        }
        return bucket.tryConsumeAndReturnRemaining(1)
    }

    @Scheduled(fixedDelay = 60_000)
    fun cleanUp() {
        groups.values.forEach { it.buckets.cleanUp() }
    }
}
