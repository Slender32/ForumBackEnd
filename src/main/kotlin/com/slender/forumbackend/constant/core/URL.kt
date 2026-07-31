package com.slender.forumbackend.constant.core

object URL {
    const val LOGOUT = "/users/*/logout"

    const val CAPTCHA = "/auth/captcha"
    const val LOGIN = "/auth/login"
    const val REGISTER = "/auth/register"
    const val REFRESH = "/auth/refresh"
    const val OPENAPI_API_DOCS_YAML = "/openapi/api-docs.yaml"
    const val OPENAPI_API_DOCS_ALL = "/openapi/api-docs/**"
    const val SWAGGER_UI_ALL = "/openapi/swagger-ui/**"
    const val STATIC_OPENAPI_DOCS = "/docs/**"
    const val WEBJARS = "/webjars/**"

    val NO_AUTH_PATHS = arrayOf(
        CAPTCHA,
        LOGIN,
        REGISTER,
        REFRESH,
        OPENAPI_API_DOCS_YAML,
        OPENAPI_API_DOCS_ALL,
        SWAGGER_UI_ALL,
        STATIC_OPENAPI_DOCS,
        WEBJARS,
    )
}
