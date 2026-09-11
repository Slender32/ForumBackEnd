package com.slender.forumbackend.aspect

import com.slender.forumbackend.component.common.AuditLogger
import com.slender.forumbackend.model.cache.UserCache
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class AdminAuditAspect(
    private val audit: AuditLogger,
    private val request: HttpServletRequest
) {
    @Around("execution(* com.slender.forumbackend.controller.Admin*.*(..))")
    fun auditAdmin(joinPoint: ProceedingJoinPoint): Any? {
        val operator =
            joinPoint.args.filterIsInstance<UserCache>().firstOrNull()?.uid
                ?: (SecurityContextHolder.getContext().authentication?.principal as? UserCache)?.uid
        val path = request.requestURI
        val permission = permission(joinPoint)
        val sequenceBefore = audit.currentSequence()
        val summary = "method=${request.method},query=${request.queryString.orEmpty()}"
        return try {
            val result = joinPoint.proceed()
            if (audit.currentSequence() == sequenceBefore) {
                audit.log(
                    operator,
                    permission,
                    resource(path),
                    resourceId(path),
                    action(joinPoint),
                    summary,
                    "SUCCESS",
                )
            }
            result
        } catch (ex: Throwable) {
            runCatching {
                audit.log(
                    operator,
                    permission,
                    resource(path),
                    resourceId(path),
                    action(joinPoint),
                    "$summary,error=${ex.message}",
                    "FAILED",
                )
            }
            throw ex
        }
    }

    private fun permission(joinPoint: ProceedingJoinPoint): String {
        val signature = joinPoint.signature as MethodSignature
        val annotation =
            signature.method.getAnnotation(PreAuthorize::class.java)
                ?: joinPoint.target.javaClass.getAnnotation(PreAuthorize::class.java)
        return AUTHORITY.find(annotation?.value.orEmpty())?.groupValues?.get(1) ?: "admin"
    }

    private fun action(joinPoint: ProceedingJoinPoint): String =
        when (request.method) {
            "POST" -> "CREATE"
            "PUT",
            "PATCH" -> "UPDATE"
            "DELETE" -> "DELETE"
            else -> (joinPoint.signature as MethodSignature).method.name.uppercase()
        }

    private fun resource(path: String) =
        path.removePrefix("/admin/").substringBefore('/').ifBlank { "admin" }

    private fun resourceId(path: String): String? =
        path.substringAfterLast('/').takeIf { it.toLongOrNull() != null }

    private companion object {
        val AUTHORITY = Regex("hasAuthority\\('([^']+)'\\)")
    }
}