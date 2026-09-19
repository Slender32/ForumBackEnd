package com.slender.forumbackend

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class PointsResponseLogging : ResponseBodyAdvice<Any> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>) = true

    override fun beforeBodyWrite(
        body: Any?, returnType: MethodParameter, selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest, response: ServerHttpResponse,
    ): Any? {
        val path = request.uri.path
        if (body is Response<*> && (path == "/user/me/points" || path == "/user/me/check-in" ||
                path.startsWith("/announcements") || path.matches(Regex("/article/[0-9]+/reward")))) {
            val uid = (SecurityContextHolder.getContext().authentication?.principal as? UserCache)?.uid
            val status = (response as? ServletServerHttpResponse)?.servletResponse?.status
            logger.info("method={} path={} status={} code={} uid={}", request.method, path, status, body.code, uid)
        }
        return body
    }
}