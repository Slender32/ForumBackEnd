package com.slender.forumbackend.exception

import org.springframework.security.core.AuthenticationException

class ValidationException(message: String) : AuthenticationException(message)