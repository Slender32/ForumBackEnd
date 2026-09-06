package com.slender.forumbackend

import com.slender.forumbackend.constant.enumeration.error.Error
import com.slender.forumbackend.exception.ArticleAlreadyRewardedException
import com.slender.forumbackend.exception.ArticleException
import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.exception.ArticleSelfRewardException
import com.slender.forumbackend.exception.ArticleTagInvalidException
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.CaptchaInvalidException
import com.slender.forumbackend.exception.CommentContentInvalidException
import com.slender.forumbackend.exception.CommentDepthExceededException
import com.slender.forumbackend.exception.CommentException
import com.slender.forumbackend.exception.CommentForbiddenException
import com.slender.forumbackend.exception.CommentNotFoundException
import com.slender.forumbackend.exception.ConversationException
import com.slender.forumbackend.exception.ConversationForbiddenException
import com.slender.forumbackend.exception.ConversationNotFoundException
import com.slender.forumbackend.exception.FileException
import com.slender.forumbackend.exception.FileNameException
import com.slender.forumbackend.exception.FileNotFoundException
import com.slender.forumbackend.exception.FileUploadFailureException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.exception.JsonException
import com.slender.forumbackend.exception.LoginException
import com.slender.forumbackend.exception.LoginExpiredException
import com.slender.forumbackend.exception.LoginMisMatchException
import com.slender.forumbackend.exception.MessageSendFailedException
import com.slender.forumbackend.exception.MessageTargetInvalidException
import com.slender.forumbackend.exception.MoePointNotEnoughException
import com.slender.forumbackend.exception.NullFileNameException
import com.slender.forumbackend.exception.ReportDuplicatedException
import com.slender.forumbackend.exception.ReportException
import com.slender.forumbackend.exception.ReportReasonInvalidException
import com.slender.forumbackend.exception.ReportSelfException
import com.slender.forumbackend.exception.RequestContentException
import com.slender.forumbackend.exception.TokenNotFoundException
import com.slender.forumbackend.exception.UserAlreadyExistsException
import com.slender.forumbackend.exception.UserException
import com.slender.forumbackend.exception.UserNotFoundException
import com.slender.forumbackend.exception.ValidationException
import com.slender.forumbackend.model.data.Response
import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
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
        is BlockException -> Error.USER_BLOCKED
        is LoginExpiredException -> Error.ACCESS_TOKEN_EXPIRED
        is TokenNotFoundException -> Error.TOKEN_MISSING
        is LoginMisMatchException -> Error.LOGIN_MISMATCH
        is RequestContentException -> Error.REQUEST_CONTENT_INVALID
    }.toResponseEntity()

    @ExceptionHandler
    fun handleValidation(exception: ValidationException) =
        Error.REQUEST_CONTENT_INVALID.toResponseEntity(exception.message)

    @ExceptionHandler
    fun handleConstraintViolation(exception: ConstraintViolationException) =
        Error.REQUEST_CONTENT_INVALID.toResponseEntity(exception.message)

    @ExceptionHandler
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException) =
        Error.REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.message)

    @ExceptionHandler
    fun handleBind(exception: BindException) =
        Error.REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.message)

    @ExceptionHandler
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException)
        = Error.REQUEST_CONTENT_INVALID.toResponseEntity()

    @ExceptionHandler
    fun handleUser(exception: UserException) = when (exception) {
        is UserNotFoundException -> Error.USER_NOT_FOUND
        is UserAlreadyExistsException -> Error.USER_ALREADY_EXISTS
        is CaptchaInvalidException -> Error.CAPTCHA_INVALID
    }.toResponseEntity()

    @ExceptionHandler
    fun handleArticle(exception: ArticleException) = when (exception) {
        is ArticleNotFoundException -> Error.ARTICLE_NOT_FOUND
        is ArticleAlreadyRewardedException -> Error.ARTICLE_ALREADY_REWARDED
        is MoePointNotEnoughException -> Error.MOE_POINT_NOT_ENOUGH
        is ArticleSelfRewardException -> Error.AUTHORITY_INSUFFICIENT
        is ArticleTagInvalidException -> Error.ARTICLE_TAG_INVALID
    }.toResponseEntity()

    @ExceptionHandler
    fun handleReport(exception: ReportException) = when (exception) {
        is ReportDuplicatedException -> Error.REPORT_DUPLICATED
        is ReportReasonInvalidException -> Error.REPORT_REASON_INVALID
        is ReportSelfException -> Error.REQUEST_CONTENT_INVALID
    }.toResponseEntity()

    @ExceptionHandler
    fun handleConversation(exception: ConversationException) = when (exception) {
        is ConversationNotFoundException -> Error.CONVERSATION_NOT_FOUND
        is MessageSendFailedException -> Error.MESSAGE_SEND_FAILED
        is MessageTargetInvalidException -> Error.MESSAGE_TARGET_INVALID
        is ConversationForbiddenException -> Error.AUTHORITY_INSUFFICIENT
    }.toResponseEntity()

    @ExceptionHandler
    fun handleComment(exception: CommentException) = when (exception) {
        is CommentNotFoundException -> Error.COMMENT_NOT_FOUND
        is CommentContentInvalidException -> Error.COMMENT_CONTENT_INVALID
        is CommentDepthExceededException -> Error.COMMENT_DEPTH_EXCEEDED
        is CommentForbiddenException -> Error.COMMENT_FORBIDDEN
    }.toResponseEntity()

    @ExceptionHandler
    fun handleFile(exception: FileException) = when (exception) {
        is NullFileNameException, is FileNameException -> Error.REQUEST_CONTENT_INVALID
        is FileNotFoundException -> Error.FILE_NOT_FOUND
        is FileUploadFailureException -> Error.INTERNAL
    }.toResponseEntity()

    @ExceptionHandler
    fun handleInvalidRequest(exception: InvalidRequestException) =
        Error.REQUEST_CONTENT_INVALID.toResponseEntity(exception.message)

    @ExceptionHandler
    fun handleJson(e: JsonException) = Error.REQUEST_CONTENT_INVALID.toResponseEntity()

    @ExceptionHandler
    fun handleAccessDenied(e: AccessDeniedException) = Error.AUTHORITY_INSUFFICIENT.toResponseEntity()

    @ExceptionHandler
    fun handleThrowable(e: Throwable) = Error.INTERNAL.toResponseEntity()

    private companion object {
        fun Error.toResponseEntity(message: String? = null)
                = ResponseEntity.status(status).body(Response.fail<Unit>(this, message))

        val BindingResult.message
            get() = fieldError?.defaultMessage ?: globalError?.defaultMessage
    }
}
