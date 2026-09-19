package com.slender.forumbackend.security.filter

import com.slender.forumbackend.constant.enumeration.config.TokenPolicy.Required
import com.slender.forumbackend.constant.enumeration.error.Error
import com.slender.forumbackend.library.RequireToken.tokenPolicy
import com.slender.forumbackend.library.logger
import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import org.springframework.util.PathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.System.currentTimeMillis
import java.time.Duration.ofSeconds
import kotlin.math.ceil

@Component
class PublicRateLimitFilter(
    private val redis: StringRedisTemplate,
    private val pathMatcher: PathMatcher,
    private val writer: Writer,
    private val properties: PublicRateLimitProperties
) : OncePerRequestFilter() {

    init { properties.validate() }
    private companion object {
        val SCRIPT = DefaultRedisScript(
            """
            local capacity = tonumber(ARGV[1])
            local refill = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local ttl = tonumber(ARGV[4])
            local tokens = tonumber(redis.call('HGET', KEYS[1], 'tokens'))
            local updated = tonumber(redis.call('HGET', KEYS[1], 'updated'))
            if tokens == nil or updated == nil then
                tokens = capacity
                updated = now
            end
            tokens = math.min(capacity, tokens + (now - updated) * refill / 1000)
            if tokens < 1 then
                redis.call('HSET', KEYS[1], 'tokens', tokens, 'updated', now)
                redis.call('EXPIRE', KEYS[1], ttl)
                return {0, math.ceil((1 - tokens) * 1000 / refill)}
            end
            redis.call('HSET', KEYS[1], 'tokens', tokens - 1, 'updated', now)
            redis.call('EXPIRE', KEYS[1], ttl)
            return {1, 0}
            """.trimIndent(), List::class.java
        )
    }

    private final val log = logger()

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (request.method == "OPTIONS" || tokenPolicy(request.requestURI, request.method, pathMatcher) == Required) {
            chain.doFilter(request, response)
            return
        }

        val (group, bucket) = properties.rule(request.servletPath.ifEmpty { request.requestURI })
        val capacity = bucket.capacity
        val refillPerSecond = bucket.refillPerMinute / 60.0
        val windowTtl = ofSeconds(ceil(capacity / refillPerSecond).toLong() + 1)
        val key = "${properties.keyPrefix}${request.remoteAddr ?: "unknown"}:$group"
        val (allowed, waitMillis) = try {
            val result = requireNotNull(redis.execute(
                SCRIPT,
                listOf(key),
                capacity.toString(),
                refillPerSecond.toString(),
                currentTimeMillis().toString(),
                windowTtl.seconds.toString(),
            )) { "Rate-limit script returned no result" }
            ((result[0] as Number).toLong() == 1L) to (result[1] as Number).toLong()
        } catch (e: Exception) {
            log.warn("Public rate-limit check failed; allowing request", e)
            true to 0L
        }

        response.setHeader("X-RateLimit-Limit", capacity.toString())
        if (!allowed) {
            response.setHeader(
                "Retry-After",
                ceil(waitMillis / 1000.0).toLong().coerceAtLeast(1).toString()
            )
            writer.write(ExceptionAdvice(Error.RATE_LIMITED), response)
            return
        }
        chain.doFilter(request, response)
    }
}
