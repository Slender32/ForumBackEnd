package com.slender.forumbackend.constant.enumeration.error

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_NOT_FOUND
import com.slender.forumbackend.constant.core.Message.Exception.ACCESS_TOKEN_EXPIRE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REFRESH_TOKEN_EXPIRE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.BLOCK_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_SIGNATURE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.EMAIL_OR_PASSWORD_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REQUEST_CONTENT_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.AUTHORITY_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.LOGIN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.UNKNOWN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.USER_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.EMAIL_REGISTERED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.CAPTCHA_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ARTICLE_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.INTERNAL_ERROR

enum class Error(
    val code: Int,
    val status: HttpStatus,
    val message: String,
) {
    TOKEN_MISSING(1001, UNAUTHORIZED, TOKEN_NOT_FOUND),
    ACCESS_TOKEN_EXPIRED(1002, UNAUTHORIZED, ACCESS_TOKEN_EXPIRE_ERROR),
    REFRESH_TOKEN_EXPIRED(1003, UNAUTHORIZED, REFRESH_TOKEN_EXPIRE_ERROR),
    USER_BLOCKED(1005, FORBIDDEN, BLOCK_ERROR),
    TOKEN_INVALID(1006, BAD_REQUEST, TOKEN_SIGNATURE_ERROR),
    LOGIN_MISMATCH(1007, BAD_REQUEST, EMAIL_OR_PASSWORD_ERROR),
    REQUEST_CONTENT_INVALID(1008, BAD_REQUEST, REQUEST_CONTENT_ERROR),
    AUTHORITY_INSUFFICIENT(1009, FORBIDDEN, AUTHORITY_ERROR),
    LOGIN_FAILED(1010, BAD_REQUEST, LOGIN_ERROR),
    UNAUTHENTICATED(1011, UNAUTHORIZED, UNKNOWN_ERROR),
    USER_NOT_FOUND(1101, NOT_FOUND, USER_NOT_FOUND_ERROR),
    USER_ALREADY_EXISTS(1102, CONFLICT, EMAIL_REGISTERED_ERROR),
    CAPTCHA_INVALID(1103, BAD_REQUEST, CAPTCHA_INVALID_ERROR),
    ARTICLE_NOT_FOUND(1201, NOT_FOUND, ARTICLE_NOT_FOUND_ERROR),
    INTERNAL(1500, INTERNAL_SERVER_ERROR, INTERNAL_ERROR),
}
