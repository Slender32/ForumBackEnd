package com.slender.forumbackend.constant.core

import com.slender.forumbackend.constant.core.TimeUnit.DAY
import com.slender.forumbackend.constant.core.TimeUnit.HOUR

object Jwt {
    const val ACCESS_TOKEN_EXPIRATION_TIME = HOUR
    const val REFRESH_TOKEN_EXPIRATION_TIME = DAY * 3

    const val ACCESS_KEY = "forum-backend-access-token-secret-slender-2026-security-key-0001"
    const val REFRESH_KEY = "forum-backend-refresh-token-secret-slender-2026-security-key-0001"
}
