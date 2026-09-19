package com.slender.forumbackend.constant.core

object Http {
    object Method {
        const val GET = "GET"
        const val POST = "POST"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
        const val PATCH = "PATCH"

        val ALL_METHODS = arrayOf(GET, POST, PUT, DELETE, PATCH)
    }


    const val LOGOUT = "/users/*/logout"
    const val CAPTCHA = "/auth/captcha"
    const val LOGIN = "/auth/login"
    const val REGISTER = "/auth/register"
    const val FORGOT_PASSWORD = "/auth/forgot-password"
    const val REFRESH = "/auth/refresh"
    const val APP_VERSION_CHECK = "/app/version/check"
    const val WEBSITE_INTRODUCTION = "/website/introduction"
    const val WEBSITE_RELEASES = "/website/releases"

    const val USER_ME = "/user/me"
    const val USER_ME_POINTS = "/user/me/points"
    const val USER_ME_CHECK_IN = "/user/me/check-in"
    const val USER_ME_AVATAR = "/user/me/avatar"
    const val USER_ME_SIGNATURE = "/user/me/signature"
    const val USER_ME_EMAIL = "/user/me/email"
    const val USER_ME_PASSWORD = "/user/me/password"

    const val ARTICLE_LIST = "/article/list"
    const val ARTICLE_DETAIL = "/article/*"
    const val ARTICLE_PUBLISH = "/article/publish"
    const val ARTICLE_PROMOTION = "/article/*/promotion"
    const val ARTICLE_LIKE = "/article/*/like"
    const val ARTICLE_REACTION = "/article/*/reaction"
    const val ARTICLE_REWARD = "/article/*/reward"
    const val ARTICLE_REPORT = "/article/*/report"
    const val ARTICLE_FAVORITE = "/article/*/favorite"
    const val ARTICLE_SEARCH = "/article/search"
    const val ARTICLE_SEARCH_SUGGESTION = "/article/search/suggestion"
    const val ARTICLE_COMMENT = "/article/*/comment"

    const val COMMENT_WILDCARD = "/comment/*"
    const val COMMENT_LIKE = "/comment/*/like"
    const val COMMENT_REPLY = "/comment/*/reply"
    const val COMMENT_REPORT = "/comment/*/report"

    const val USERS_PROFILE = "/users/*/profile"
    const val USERS_ARTICLE = "/users/*/article"
    const val USERS_COMMENT = "/users/*/comment"
    const val USERS_FAVORITE = "/users/*/favorite"
    const val USERS_FOLLOW = "/users/*/follow"
    const val USERS_REPORT = "/users/*/report"

    const val CONVERSATION = "/conversation"
    const val CONVERSATION_LIST = "/conversation/list"
    const val CONVERSATION_MESSAGE = "/conversation/*/message"
    const val CONVERSATION_READ = "/conversation/*/read"
    const val MESSAGE_WEBSOCKET = "/ws/message"

    const val NOTICE_COMMENT = "/notice/comment"
    const val NOTICE_COMMENT_READ = "/notice/comment/read"

    const val TAG_LIST = "/tag/list"
    const val TAG_WILDCARD = "/tag/*"

    const val HOME_CAROUSEL = "/home/carousel"
    const val ANNOUNCEMENTS = "/announcements"
    const val ANNOUNCEMENT_READ = "/announcements/*/read"

    const val FILES_IMAGE_UPLOAD = "/files/image/upload"
    const val FILES_IMAGE_METADATA = "/files/image/metadata"

    const val OPENAPI_API_DOCS_YAML = "/openapi/api-docs.yaml"
    const val OPENAPI_API_DOCS_ALL = "/openapi/api-docs/**"
    const val SWAGGER_UI_ALL = "/openapi/swagger-ui/**"
    const val STATIC_OPENAPI_DOCS = "/docs/**"
    const val WEBJARS = "/webjars/**"

    val NO_AUTH_PATHS = arrayOf(
        CAPTCHA,
        LOGIN,
        REGISTER,
        FORGOT_PASSWORD,
        REFRESH,
        APP_VERSION_CHECK,
        WEBSITE_INTRODUCTION,
        WEBSITE_RELEASES,
        ARTICLE_LIST,
        ARTICLE_DETAIL,
        OPENAPI_API_DOCS_YAML,
        OPENAPI_API_DOCS_ALL,
        SWAGGER_UI_ALL,
        STATIC_OPENAPI_DOCS,
        WEBJARS,
        ARTICLE_SEARCH,
        ARTICLE_SEARCH_SUGGESTION,
        ARTICLE_COMMENT,
        COMMENT_REPLY,
        USERS_PROFILE,
        USERS_ARTICLE,
        USERS_COMMENT,
        TAG_LIST,
        TAG_WILDCARD,
        HOME_CAROUSEL,
        ANNOUNCEMENTS,
        MESSAGE_WEBSOCKET,
        FILES_IMAGE_METADATA,
    )

    val AUTH_PATHS = arrayOf(
        ARTICLE_PUBLISH,
        ARTICLE_PROMOTION,
        ARTICLE_LIKE,
        ARTICLE_REACTION,
        ARTICLE_REWARD,
        ARTICLE_REPORT,
        COMMENT_LIKE,
        COMMENT_REPLY,
        COMMENT_REPORT,
        ARTICLE_COMMENT,
        FILES_IMAGE_UPLOAD,
        USERS_REPORT,
        CONVERSATION,
        CONVERSATION_LIST,
        CONVERSATION_MESSAGE,
        CONVERSATION_READ,
        COMMENT_WILDCARD,
        NOTICE_COMMENT,
        NOTICE_COMMENT_READ,
        USERS_FAVORITE,
        USERS_FOLLOW,
        ARTICLE_FAVORITE,
        USER_ME,
        USER_ME_POINTS,
        USER_ME_CHECK_IN,
        USER_ME_AVATAR,
        USER_ME_SIGNATURE,
        USER_ME_EMAIL,
        USER_ME_PASSWORD,
        ANNOUNCEMENT_READ,
    )
}

