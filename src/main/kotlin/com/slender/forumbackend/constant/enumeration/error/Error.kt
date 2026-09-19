package com.slender.forumbackend.constant.enumeration.error

import com.slender.forumbackend.constant.core.Message.Exception.ALREADY_CHECKED_IN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ACCESS_TOKEN_EXPIRE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ANNOUNCEMENT_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ADMIN_RESOURCE_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ARTICLE_ALREADY_REWARDED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ARTICLE_CONTENT_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ARTICLE_FORBIDDEN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ARTICLE_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.ARTICLE_TAG_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.AUTHORITY_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.BLOCK_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.CAPTCHA_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.COMMENT_CONTENT_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.COMMENT_DEPTH_EXCEEDED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.COMMENT_FORBIDDEN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.COMMENT_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.CONVERSATION_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.CONVERSATION_FOLLOW_REQUIRED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.EMAIL_OR_PASSWORD_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.EMAIL_REGISTERED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.FILE_NOT_FOUND_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.INTERNAL_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.LOGIN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.MESSAGE_SEND_FAILED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.MESSAGE_TARGET_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.MOE_POINT_NOT_ENOUGH_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.RATE_LIMITED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REFRESH_TOKEN_EXPIRE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REPORT_DUPLICATED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REPORT_REASON_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.REQUEST_CONTENT_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_NOT_FOUND
import com.slender.forumbackend.constant.core.Message.Exception.TOKEN_SIGNATURE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.UNKNOWN_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.UPLOAD_FAILED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.UPLOAD_TOO_LARGE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.UPLOAD_TYPE_UNSUPPORTED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.USER_NOT_FOUND_ERROR
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.TOO_MANY_REQUESTS
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED

enum class Error(val code: Int, val status: HttpStatus, val message: String) {
    ALREADY_CHECKED_IN(1104, CONFLICT, ALREADY_CHECKED_IN_ERROR),
    ANNOUNCEMENT_NOT_FOUND(1901, NOT_FOUND, ANNOUNCEMENT_NOT_FOUND_ERROR),
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
    ARTICLE_FORBIDDEN(1202, FORBIDDEN, ARTICLE_FORBIDDEN_ERROR),
    ARTICLE_CONTENT_INVALID(1203, BAD_REQUEST, ARTICLE_CONTENT_INVALID_ERROR),
    ARTICLE_ALREADY_REWARDED(1204, BAD_REQUEST, ARTICLE_ALREADY_REWARDED_ERROR),
    MOE_POINT_NOT_ENOUGH(1205, BAD_REQUEST, MOE_POINT_NOT_ENOUGH_ERROR),
    ARTICLE_TAG_INVALID(1206, BAD_REQUEST, ARTICLE_TAG_INVALID_ERROR),
    COMMENT_NOT_FOUND(1301, NOT_FOUND, COMMENT_NOT_FOUND_ERROR),
    COMMENT_CONTENT_INVALID(1302, BAD_REQUEST, COMMENT_CONTENT_INVALID_ERROR),
    COMMENT_DEPTH_EXCEEDED(1303, BAD_REQUEST, COMMENT_DEPTH_EXCEEDED_ERROR),
    COMMENT_FORBIDDEN(1304, FORBIDDEN, COMMENT_FORBIDDEN_ERROR),
    FILE_NOT_FOUND(1401, NOT_FOUND, FILE_NOT_FOUND_ERROR),
    UPLOAD_TOO_LARGE(1402, PAYLOAD_TOO_LARGE, UPLOAD_TOO_LARGE_ERROR),
    UPLOAD_TYPE_UNSUPPORTED(1403, UNSUPPORTED_MEDIA_TYPE, UPLOAD_TYPE_UNSUPPORTED_ERROR),
    UPLOAD_FAILED(1404, INTERNAL_SERVER_ERROR, UPLOAD_FAILED_ERROR),
    INTERNAL(1500, INTERNAL_SERVER_ERROR, INTERNAL_ERROR),
    CONVERSATION_NOT_FOUND(1501, NOT_FOUND, CONVERSATION_NOT_FOUND_ERROR),
    MESSAGE_SEND_FAILED(1502, INTERNAL_SERVER_ERROR, MESSAGE_SEND_FAILED_ERROR),
    MESSAGE_TARGET_INVALID(1503, FORBIDDEN, MESSAGE_TARGET_INVALID_ERROR),
    CONVERSATION_FOLLOW_REQUIRED(1504, FORBIDDEN, CONVERSATION_FOLLOW_REQUIRED_ERROR),
    REPORT_DUPLICATED(1601, CONFLICT, REPORT_DUPLICATED_ERROR),
    REPORT_REASON_INVALID(1602, BAD_REQUEST, REPORT_REASON_INVALID_ERROR),
    RATE_LIMITED(1701, TOO_MANY_REQUESTS, RATE_LIMITED_ERROR),
    ADMIN_RESOURCE_NOT_FOUND(1801, NOT_FOUND, ADMIN_RESOURCE_NOT_FOUND_ERROR),
}
