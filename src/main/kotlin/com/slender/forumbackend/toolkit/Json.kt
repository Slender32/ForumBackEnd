package com.slender.forumbackend.toolkit

import com.slender.forumbackend.exception.JsonFormatException
import com.slender.forumbackend.exception.JsonParseException
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.io.InputStream
import kotlin.reflect.KClass

@Component
class Json(
    private val objectMapper: ObjectMapper
) {
    fun <T : Any> parse(json: String, clazz: KClass<T>): T = runCatching {
        objectMapper.readValue(json, clazz.java)
    }.onFailure {
        if(it is RuntimeException) throw JsonParseException()
    }.getOrThrow()

    fun <T : Any> parse(stream: InputStream, clazz: KClass<T>): T = runCatching {
        objectMapper.readValue(stream, clazz.java)
    }.onFailure {
        if(it is RuntimeException) throw JsonParseException()
    }.getOrThrow()

    fun format(data: Any): String = runCatching {
        objectMapper.writeValueAsString(data)
    }.onFailure {
        if(it is RuntimeException) throw JsonFormatException()
    }.getOrThrow()
}
