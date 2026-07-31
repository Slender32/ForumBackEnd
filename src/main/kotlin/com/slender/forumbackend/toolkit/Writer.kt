package com.slender.forumbackend.toolkit

import com.slender.forumbackend.model.error.ExceptionAdvice
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.fail
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Component

@Component
class Writer(
    private val json: Json
) {
    fun <T> write(
        data: Response<T>,
        response: HttpServletResponse,
        httpStatus: HttpStatus = OK
    ) = response.apply {
        status = httpStatus.value()
        contentType = "application/json;charset=utf-8"
        writer.write(json.format(data))
    }

    fun write(
        exceptionAdvice: ExceptionAdvice,
        response: HttpServletResponse,
    ) = exceptionAdvice.run {
        write(
            fail<Unit>(error, message),
            response, error.status
        )
    }
}
