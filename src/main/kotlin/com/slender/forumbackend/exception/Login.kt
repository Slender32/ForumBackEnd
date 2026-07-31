package com.slender.forumbackend.exception

import org.springframework.security.core.AuthenticationException

sealed class LoginException(message: String? = null) : AuthenticationException(message)

class BlockException : LoginException()
class LoginExpiredException : LoginException()
class LoginMisMatchException(message: String) : LoginException(message)
class RequestContentException : LoginException()
class TokenNotFoundException : LoginException()
