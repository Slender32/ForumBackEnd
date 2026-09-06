package com.slender.forumbackend.configuration

import com.slender.forumbackend.constant.core.Http.ARTICLE_COMMENT
import com.slender.forumbackend.constant.core.Http.ARTICLE_DETAIL
import com.slender.forumbackend.constant.core.Http.ARTICLE_LIST
import com.slender.forumbackend.constant.core.Http.ARTICLE_SEARCH
import com.slender.forumbackend.constant.core.Http.ARTICLE_SEARCH_SUGGESTION
import com.slender.forumbackend.constant.core.Http.AUTH_PATHS
import com.slender.forumbackend.constant.core.Http.COMMENT_REPLY
import com.slender.forumbackend.constant.core.Http.COMMENT_WILDCARD
import com.slender.forumbackend.constant.core.Http.HOME_CAROUSEL
import com.slender.forumbackend.constant.core.Http.LOGOUT
import com.slender.forumbackend.constant.core.Http.Method.ALL_METHODS
import com.slender.forumbackend.constant.core.Http.Method.DELETE
import com.slender.forumbackend.constant.core.Http.Method.GET
import com.slender.forumbackend.constant.core.Http.NO_AUTH_PATHS
import com.slender.forumbackend.constant.core.Http.TAG_LIST
import com.slender.forumbackend.constant.core.Http.TAG_WILDCARD
import com.slender.forumbackend.constant.core.Http.USERS_ARTICLE
import com.slender.forumbackend.constant.core.Http.USERS_COMMENT
import com.slender.forumbackend.constant.core.Http.USERS_PROFILE
import com.slender.forumbackend.library.*
import com.slender.forumbackend.library.RequireToken.methodPolicy
import com.slender.forumbackend.library.RequireToken.notRequireToken
import com.slender.forumbackend.library.RequireToken.requireToken
import com.slender.forumbackend.security.filter.JwtFilter
import com.slender.forumbackend.security.filter.PasswordFilter
import com.slender.forumbackend.security.handler.AccessRefuseHandler
import com.slender.forumbackend.security.handler.ExceptionHandler
import com.slender.forumbackend.security.handler.SignOutHandler
import com.slender.forumbackend.security.handler.SignOutSuccessHandler
import com.slender.forumbackend.constant.enumeration.config.TokenPolicy.Optional
import com.slender.forumbackend.constant.enumeration.config.TokenPolicy.Required
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher

@Configuration
@EnableMethodSecurity
class Security {
    init {
        notRequireToken(NO_AUTH_PATHS)
        requireToken(AUTH_PATHS)
        methodPolicy(GET, Optional, arrayOf(
            ARTICLE_LIST,
            ARTICLE_SEARCH,
            ARTICLE_SEARCH_SUGGESTION,
            ARTICLE_DETAIL,
            ARTICLE_COMMENT,
            COMMENT_REPLY,
            USERS_PROFILE,
            USERS_ARTICLE,
            USERS_COMMENT,
            TAG_LIST,
            TAG_WILDCARD,
            HOME_CAROUSEL,
        ))
        methodPolicy(DELETE, COMMENT_WILDCARD, Required)
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        jwtFilter: JwtFilter,
        passwordFilter: PasswordFilter,
        signOutHandler: SignOutHandler,
        signOutSuccessHandler: SignOutSuccessHandler,
        exceptionHandler: ExceptionHandler,
        accessRefuseHandler: AccessRefuseHandler,
    ): SecurityFilterChain = http.configure {
        corsConfiguration {
            allowedMethods = ALL_METHODS.asList()
            allowCredentials = false
            allowedOrigins = listOf("*")
            maxAge = 3600L
        }

        disableCsrf()
        noSession()
        noAuthentication(*NO_AUTH_PATHS)

        appendFilter {
            at<UsernamePasswordAuthenticationFilter>(passwordFilter)
            before<LogoutFilter>(jwtFilter)
        }

        signOut {
            logoutUrl(LOGOUT)
            addLogoutHandler(signOutHandler)
            logoutSuccessHandler(signOutSuccessHandler)
        }

        exceptionHandler {
            authenticationEntryPoint(exceptionHandler)
            accessDeniedHandler(accessRefuseHandler)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun pathMatcher(): PathMatcher = AntPathMatcher()
}
