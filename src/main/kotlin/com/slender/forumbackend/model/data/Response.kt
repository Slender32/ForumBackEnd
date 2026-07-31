package com.slender.forumbackend.model.data

import com.slender.forumbackend.constant.enumeration.error.Error
import io.swagger.v3.oas.annotations.media.Schema
import java.lang.System.currentTimeMillis

@Schema(description = "统一接口响应")
data class Response<T>(
    @field:Schema(description = "业务状态码，0表示成功，失败时见 Error", example = "0")
    val code :Int,

    @field:Schema(description = "响应消息", example = "操作成功", nullable = true)
    val message :String?,

    @field:Schema(description = "响应时间戳，单位毫秒", example = "1767225600000")
    val timestamp: Long,

    @field:Schema(description = "响应数据", nullable = true)
    val data :T?
){
    companion object {
        const val SUCCESS_CODE = 0

        fun <T> success(msg: String? = null): Response<T> =
            Response(SUCCESS_CODE, msg, currentTimeMillis(), null)

        fun <T> success(msg: String? = null, data: T): Response<T> =
            Response(SUCCESS_CODE, msg, currentTimeMillis(), data)

        fun <T> success(data: T): Response<T> =
            Response(SUCCESS_CODE, null, currentTimeMillis(), data)

        fun <T> fail(error: Error, msg: String? = null): Response<T> =
            Response(error.code, msg ?: error.message, currentTimeMillis(), null)
    }
}
