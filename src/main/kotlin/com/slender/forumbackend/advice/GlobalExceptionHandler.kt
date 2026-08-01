package com.slender.forumbackend.advice

import com.slender.forumbackend.constant.enumeration.error.Error
import com.slender.forumbackend.constant.enumeration.error.Error.*
import com.slender.forumbackend.exception.*
import com.slender.forumbackend.model.data.Response.Companion.fail
import org.springframework.http.ResponseEntity.status
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
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
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.message)

    @ExceptionHandler
    fun handleBind(exception: BindException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.message)

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
    fun handleArticle(exception: ArticleException) = when (exception) {
        is ArticleNotFoundException -> ARTICLE_NOT_FOUND
    }.toResponseEntity()

    @ExceptionHandler
    fun handleAccessDenied(e: AccessDeniedException) = AUTHORITY_INSUFFICIENT.toResponseEntity()

    private final fun Error.toResponseEntity(message: String? = null)
        = status(status).body(fail<Unit>(this, message))

    private final val BindingResult.message
        get() = fieldError?.defaultMessage ?: globalError?.defaultMessage

}
