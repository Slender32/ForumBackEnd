package com.slender.forumbackend.exception

sealed class FileException : RuntimeException()

class NullFileNameException : FileException()
class FileUploadFailureException : FileException()
class FileNameException : FileException()
class FileNotFoundException : FileException()
class InvalidRequestException(override val message: String) : RuntimeException(message)