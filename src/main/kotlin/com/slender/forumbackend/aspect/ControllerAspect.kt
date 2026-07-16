package com.slender.forumbackend.aspect

import com.slender.forumbackend.library.logger
import com.slender.forumbackend.model.data.Response
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.System.currentTimeMillis
import java.lang.reflect.Array as RfArray

@Aspect
@Order(6)
@Component
class ControllerAspect {
    private val log = logger()

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun monitorController(joinPoint: ProceedingJoinPoint): Any? {
        val startedAt = currentTimeMillis()
        val signature = joinPoint.signature as MethodSignature
        val controllerMethod = "${signature.declaringType.simpleName}.${signature.name}"
        val parameters = formatParameters(signature.parameterNames, joinPoint.args)

        log.info("Controller接口调用: method={}, params={}", controllerMethod, parameters)

        return try {
            val result = joinPoint.proceed()
            val cost = currentTimeMillis() - startedAt

            log.info(
                "Controller接口返回: method={}, cost={}ms, result={}",
                controllerMethod,
                cost,
                formatResult(result),
            )

            result
        } catch (throwable: Throwable) {
            val cost = currentTimeMillis() - startedAt

            log.error(
                "Controller接口异常: method={}, cost={}ms, message={}",
                controllerMethod,
                cost,
                throwable.message,
                throwable,
            )

            throw throwable
        }
    }

    private final fun formatParameters(
        parameterNames: Array<String>,
        args: Array<Any?>
    ): Map<String, Any?> =
        args.mapIndexed { index, arg ->
            val name = parameterNames.getOrNull(index) ?: "arg$index"
            name to arg
        }.toMap()

    private final fun formatResult(result: Any?): Any? = when {
        result == null -> null
        result.isPrimitiveArrayResult() -> result.formatPrimitiveArrayResult()
        result.isListResult() -> OMITTED_LIST_RESULT
        result is Response<*> -> formatResponse(result)
        else -> result
    }

    private fun formatResponse(response: Response<*>): String =
        "Response(code=${response.code}, message=${response.message}, timestamp=${response.timestamp}, " +
            "data=${formatResult(response.data)})"

    private fun Any.isListResult(): Boolean =
        this is Collection<*> || this is Array<*> || this is Iterable<*>

    private fun Any.isPrimitiveArrayResult(): Boolean =
        javaClass.isArray && javaClass.componentType.isPrimitive

    private fun Any.formatPrimitiveArrayResult(): String {
        val length = RfArray.getLength(this)
        val preview = (0 until minOf(length, ARRAY_PREVIEW_SIZE)).joinToString(", ") { index ->
            RfArray.get(this, index).toString()
        }
        val ellipsis = if (length > ARRAY_PREVIEW_SIZE) ",...." else ""

        return "[$preview$ellipsis]"
    }

    companion object {
        private const val OMITTED_LIST_RESULT = "<list result omitted>"
        private const val ARRAY_PREVIEW_SIZE = 6
    }
}
