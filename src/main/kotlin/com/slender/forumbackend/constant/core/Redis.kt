package com.slender.forumbackend.constant.core

import com.slender.forumbackend.constant.core.TimeUnit.HOUR
import java.time.Duration
import java.time.Duration.ofMinutes
import java.time.Duration.ofMillis

object Redis {
    object Time {
        val ACCESS_TOKEN_EXPIRE_TIME: Duration = ofMillis(HOUR)
        val REGISTER_CAPTCHA_EXPIRE_TIME: Duration = ofMinutes(5)
    }

    object Key {
        const val USER_LOGIN_CACHE = "LoginCache:"
        const val USER_BLOCK = "Block:"
        const val REGISTER_CAPTCHA = "RegisterCaptcha:"
        const val ARTICLE_LIKE_PENDING = "Pending:ArticleLike"
        const val COMMENT_LIKE_PENDING = "Pending:CommentLike"
        const val ARTICLE_REACTION_PENDING = "Pending:ArticleReaction"
        const val ARTICLE_VIEW = "View:"
        const val SEARCH_SUGGESTION = "SearchSuggestion:"
        const val HOME_CAROUSEL = "HomeCarousel"
    }
}
