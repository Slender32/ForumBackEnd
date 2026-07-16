package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema
import java.lang.System.currentTimeMillis

@Schema(description = "统一接口响应")
data class Response<T>(
    @field:Schema(description = "业务状态码，0表示成功", example = "0")
    val code :Int,

    @field:Schema(description = "响应消息", example = "操作成功", nullable = true)
    val message :String?,

    @field:Schema(description = "响应时间戳，单位毫秒", example = "1767225600000")
    val timestamp: Long,

    @field:Schema(description = "响应数据", nullable = true)
    val data :T?
){
    companion object {
        fun <T> success(msg: String? = null): Response<T> =
            Response(0, msg, currentTimeMillis(), null)

        fun <T> fail(code: Int, msg: String? = null): Response<T> =
            Response(code, msg, currentTimeMillis(), null)

        fun <T> fail(msg: String? = null): Response<T> =
            Response(500, msg, currentTimeMillis(), null)

        fun <T> success(msg: String? = null, data: T): Response<T> =
            Response(0, msg, currentTimeMillis(), data)

        fun <T> success(data: T): Response<T> =
            Response(0, null, currentTimeMillis(), data)

        fun exception(code: Int, msg: String? = null): Response<Unit> =
            Response(code, msg, currentTimeMillis(), null)

        fun exception(msg: String? = null): Response<Unit> =
            Response(500, msg, currentTimeMillis(), null)
    }
}
