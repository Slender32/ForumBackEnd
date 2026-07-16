package com.slender.forumbackend.toolkit

import com.slender.forumbackend.model.data.Response
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class Writer(
    private val json: Json
) {
    fun <T> write(
        data: Response<T>,
        response: HttpServletResponse
    ) = response.apply {
        status = if (data.code == 0) 200 else data.code
        contentType = "application/json;charset=utf-8"
        writer.write(json.format(data))
    }
}
