package com.slender.forumbackend.advice

import com.slender.forumbackend.constant.enumeration.error.Error
import com.slender.forumbackend.constant.enumeration.error.Error.ACCESS_TOKEN_EXPIRED
import com.slender.forumbackend.constant.enumeration.error.Error.AUTHORITY_INSUFFICIENT
import com.slender.forumbackend.constant.enumeration.error.Error.CAPTCHA_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.LOGIN_MISMATCH
import com.slender.forumbackend.constant.enumeration.error.Error.REQUEST_CONTENT_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.TOKEN_MISSING
import com.slender.forumbackend.constant.enumeration.error.Error.USER_ALREADY_EXISTS
import com.slender.forumbackend.constant.enumeration.error.Error.USER_BLOCKED
import com.slender.forumbackend.constant.enumeration.error.Error.USER_NOT_FOUND
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.CaptchaInvalidException
import com.slender.forumbackend.exception.LoginException
import com.slender.forumbackend.exception.LoginExpiredException
import com.slender.forumbackend.exception.LoginMisMatchException
import com.slender.forumbackend.exception.RequestContentException
import com.slender.forumbackend.exception.TokenNotFoundException
import com.slender.forumbackend.exception.UserAlreadyExistsException
import com.slender.forumbackend.exception.UserException
import com.slender.forumbackend.exception.UserNotFoundException
import com.slender.forumbackend.exception.ValidationException
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.fail
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    fun handleLogin(exception: LoginException) = when (exception) {
        is BlockException -> USER_BLOCKED
        is LoginExpiredException -> ACCESS_TOKEN_EXPIRED
        is TokenNotFoundException -> TOKEN_MISSING
        is LoginMisMatchException -> LOGIN_MISMATCH
        is RequestContentException -> REQUEST_CONTENT_INVALID
    }.toResponseEntity()

    @ExceptionHandler
    fun handleValidation(exception: ValidationException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.message)

    @ExceptionHandler
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.fieldError?.defaultMessage)

    @ExceptionHandler
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException)
        = REQUEST_CONTENT_INVALID.toResponseEntity()

    @ExceptionHandler
    fun handleUser(exception: UserException) = when (exception) {
        is UserNotFoundException -> USER_NOT_FOUND
        is UserAlreadyExistsException -> USER_ALREADY_EXISTS
        is CaptchaInvalidException -> CAPTCHA_INVALID
    }.toResponseEntity()

    @ExceptionHandler
    fun handleAccessDenied(e: AccessDeniedException) = AUTHORITY_INSUFFICIENT.toResponseEntity()

    private final fun Error.toResponseEntity(message: String? = null) : ResponseEntity<Response<Unit>>
        = status(status).body(fail(this, message))

}
