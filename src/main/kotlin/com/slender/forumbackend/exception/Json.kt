package com.slender.forumbackend.exception

sealed class JsonException : RuntimeException()

class JsonFormatException : JsonException()
class JsonParseException : JsonException()