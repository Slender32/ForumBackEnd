package com.slender.forumbackend.security.filter

import com.slender.forumbackend.constant.core.Http.ARTICLE_SEARCH
import com.slender.forumbackend.constant.core.Http.ARTICLE_SEARCH_SUGGESTION
import com.slender.forumbackend.constant.core.Http.CAPTCHA
import com.slender.forumbackend.constant.core.Http.FORGOT_PASSWORD
import com.slender.forumbackend.constant.core.Http.LOGIN
import com.slender.forumbackend.constant.core.Http.REGISTER
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("forum.rate-limit")
class PublicRateLimitProperties {
    var keyPrefix: String = "RateLimit:public:v2:"
    var browsing = Bucket(60, 120)
    var search = Bucket(10, 30)
    var login = Bucket(10, 10)
    var captcha = Bucket(2, 1)
    var account = Bucket(5, 5)

    fun rule(path: String): Pair<String, Bucket> = when (path) {
        CAPTCHA -> "captcha" to captcha
        LOGIN -> "login" to login
        REGISTER, FORGOT_PASSWORD -> "account" to account
        ARTICLE_SEARCH, ARTICLE_SEARCH_SUGGESTION -> "search" to search
        else -> "browsing" to browsing
    }

    fun validate() {
        listOf(browsing, search, login, captcha, account).forEach {
            require(it.capacity > 0 && it.refillPerMinute > 0) {
                "Rate-limit capacity and refill-per-minute must be positive"
            }
        }
    }

    data class Bucket(var capacity: Long = 60, var refillPerMinute: Long = 120)
}
