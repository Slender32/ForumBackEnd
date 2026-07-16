package com.slender.forumbackend.constant.core

import com.slender.forumbackend.constant.core.TimeUnit.HOUR
import java.time.Duration
import java.time.Duration.ofMillis

object Redis {
    object Time {
        val ACCESS_TOKEN_EXPIRE_TIME: Duration = ofMillis(HOUR)
    }

    object Key {
        const val USER_LOGIN_CACHE = "LoginCache:"
        const val USER_BLOCK = "Block:"
    }
}
