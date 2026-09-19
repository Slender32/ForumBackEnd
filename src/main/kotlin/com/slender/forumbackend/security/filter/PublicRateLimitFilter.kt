package com.slender.forumbackend.security.filter

import com.slender.forumbackend.constant.enumeration.config.TokenPolicy.Required
import com.slender.forumbackend.constant.enumeration.error.Error
import com.slender.forumbackend.library.RequireToken.tokenPolicy
import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.toolkit.Writer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.PathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.TimeUnit

@Component
class PublicRateLimitFilter(
    private val limiter: LocalIpRateLimiter,
    private val pathMatcher: PathMatcher,
    private val writer: Writer,
    private val properties: PublicRateLimitProperties,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (request.method == "OPTIONS" || tokenPolicy(request.requestURI, request.method, pathMatcher) == Required) {
            chain.doFilter(request, response)
            return
        }

        val (group, bucket) = properties.rule(request.servletPath.ifEmpty { request.requestURI })
        val probe = limiter.consume(request.remoteAddr ?: "unknown", group)
        response.setHeader("X-RateLimit-Limit", bucket.capacity.toString())
        if (!probe.isConsumed) {
            val nanos = probe.nanosToWaitForRefill
            val second = TimeUnit.SECONDS.toNanos(1)
            val waitSeconds = nanos / second + if (nanos % second == 0L) 0 else 1
            response.setHeader("Retry-After", waitSeconds.coerceAtLeast(1).toString())
            writer.write(ExceptionAdvice(Error.RATE_LIMITED), response)
            return
        }
        chain.doFilter(request, response)
    }
}
