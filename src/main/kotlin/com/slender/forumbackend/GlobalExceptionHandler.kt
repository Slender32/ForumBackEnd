package com.slender.forumbackend

import com.slender.forumbackend.constant.enumeration.error.Error
import com.slender.forumbackend.constant.enumeration.error.Error.ACCESS_TOKEN_EXPIRED
import com.slender.forumbackend.constant.enumeration.error.Error.ADMIN_RESOURCE_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.ALREADY_CHECKED_IN
import com.slender.forumbackend.constant.enumeration.error.Error.ANNOUNCEMENT_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.ARTICLE_ALREADY_REWARDED
import com.slender.forumbackend.constant.enumeration.error.Error.ARTICLE_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.ARTICLE_TAG_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.AUTHORITY_INSUFFICIENT
import com.slender.forumbackend.constant.enumeration.error.Error.CAPTCHA_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.COMMENT_CONTENT_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.COMMENT_DEPTH_EXCEEDED
import com.slender.forumbackend.constant.enumeration.error.Error.COMMENT_FORBIDDEN
import com.slender.forumbackend.constant.enumeration.error.Error.COMMENT_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.CONVERSATION_FOLLOW_REQUIRED
import com.slender.forumbackend.constant.enumeration.error.Error.CONVERSATION_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.FILE_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.INTERNAL
import com.slender.forumbackend.constant.enumeration.error.Error.LOGIN_MISMATCH
import com.slender.forumbackend.constant.enumeration.error.Error.MESSAGE_SEND_FAILED
import com.slender.forumbackend.constant.enumeration.error.Error.MESSAGE_TARGET_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.MOE_POINT_NOT_ENOUGH
import com.slender.forumbackend.constant.enumeration.error.Error.REPORT_DUPLICATED
import com.slender.forumbackend.constant.enumeration.error.Error.REPORT_REASON_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.REQUEST_CONTENT_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.TOKEN_MISSING
import com.slender.forumbackend.constant.enumeration.error.Error.USER_ALREADY_EXISTS
import com.slender.forumbackend.constant.enumeration.error.Error.USER_BLOCKED
import com.slender.forumbackend.constant.enumeration.error.Error.USER_NOT_FOUND
import com.slender.forumbackend.exception.AdminResourceNotFoundException
import com.slender.forumbackend.exception.AlreadyCheckedInException
import com.slender.forumbackend.exception.AnnouncementNotFoundException
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
import com.slender.forumbackend.exception.ConversationFollowRequiredException
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
    fun handleAlreadyCheckedIn(e: AlreadyCheckedInException) =
        ALREADY_CHECKED_IN.toResponseEntity()

    @ExceptionHandler
    fun handleAnnouncementNotFound(e: AnnouncementNotFoundException) =
        ANNOUNCEMENT_NOT_FOUND.toResponseEntity()

    @ExceptionHandler
    fun handleLogin(exception: LoginException) =
        when (exception) {
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
    fun handleConstraintViolation(exception: ConstraintViolationException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.message)

    @ExceptionHandler
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.message)

    @ExceptionHandler
    fun handleBind(exception: BindException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.bindingResult.message)

    @ExceptionHandler
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException) =
        REQUEST_CONTENT_INVALID.toResponseEntity()

    @ExceptionHandler
    fun handleUser(exception: UserException) =
        when (exception) {
            is UserNotFoundException -> USER_NOT_FOUND
            is UserAlreadyExistsException -> USER_ALREADY_EXISTS
            is CaptchaInvalidException -> CAPTCHA_INVALID
        }.toResponseEntity()

    @ExceptionHandler
    fun handleArticle(exception: ArticleException) =
        when (exception) {
            is ArticleNotFoundException -> ARTICLE_NOT_FOUND
            is ArticleAlreadyRewardedException -> ARTICLE_ALREADY_REWARDED
            is MoePointNotEnoughException -> MOE_POINT_NOT_ENOUGH
            is ArticleSelfRewardException -> AUTHORITY_INSUFFICIENT
            is ArticleTagInvalidException -> ARTICLE_TAG_INVALID
        }.toResponseEntity()

    @ExceptionHandler
    fun handleReport(exception: ReportException) =
        when (exception) {
            is ReportDuplicatedException -> REPORT_DUPLICATED
            is ReportReasonInvalidException -> REPORT_REASON_INVALID
            is ReportSelfException -> REQUEST_CONTENT_INVALID
        }.toResponseEntity()

    @ExceptionHandler
    fun handleConversation(exception: ConversationException) =
        when (exception) {
            is ConversationNotFoundException -> CONVERSATION_NOT_FOUND
            is MessageSendFailedException -> MESSAGE_SEND_FAILED
            is MessageTargetInvalidException -> MESSAGE_TARGET_INVALID
            is ConversationForbiddenException -> AUTHORITY_INSUFFICIENT
            is ConversationFollowRequiredException -> CONVERSATION_FOLLOW_REQUIRED
        }.toResponseEntity()

    @ExceptionHandler
    fun handleComment(exception: CommentException) =
        when (exception) {
            is CommentNotFoundException -> COMMENT_NOT_FOUND
            is CommentContentInvalidException -> COMMENT_CONTENT_INVALID
            is CommentDepthExceededException -> COMMENT_DEPTH_EXCEEDED
            is CommentForbiddenException -> COMMENT_FORBIDDEN
        }.toResponseEntity()

    @ExceptionHandler
    fun handleFile(exception: FileException) =
        when (exception) {
            is NullFileNameException,
            is FileNameException -> REQUEST_CONTENT_INVALID
            is FileNotFoundException -> FILE_NOT_FOUND
            is FileUploadFailureException -> INTERNAL
        }.toResponseEntity()

    @ExceptionHandler
    fun handleInvalidRequest(exception: InvalidRequestException) =
        REQUEST_CONTENT_INVALID.toResponseEntity(exception.message)

    @ExceptionHandler
    fun handleJson(e: JsonException) = REQUEST_CONTENT_INVALID.toResponseEntity()

    @ExceptionHandler
    fun handleAccessDenied(e: AccessDeniedException) =
        AUTHORITY_INSUFFICIENT.toResponseEntity()

    @ExceptionHandler
    fun handleAdminResourceNotFound(e: AdminResourceNotFoundException) =
        ADMIN_RESOURCE_NOT_FOUND.toResponseEntity(e.message)

    @ExceptionHandler fun handleThrowable(e: Throwable) = INTERNAL.toResponseEntity()

    private companion object {
        fun Error.toResponseEntity(message: String? = null) =
            ResponseEntity.status(status).body(Response.fail<Unit>(this, message))

        val BindingResult.message
            get() = fieldError?.defaultMessage ?: globalError?.defaultMessage
    }
}
