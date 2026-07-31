package com.slender.forumbackend.model.error

import com.slender.forumbackend.constant.enumeration.error.Error

data class ExceptionAdvice(
    val error: Error,
    val message: String? = null
)