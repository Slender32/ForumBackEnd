package com.slender.forumbackend.exception

sealed class UserException: RuntimeException()

class UserNotFoundException: UserException()
class UserAlreadyExistsException: UserException()
class CaptchaInvalidException: UserException()
