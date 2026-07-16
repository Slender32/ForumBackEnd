package com.slender.forumbackend.configuration

import com.slender.forumbackend.constant.core.URL.LOGOUT
import com.slender.forumbackend.constant.core.URL.NO_AUTH_PATHS
import com.slender.forumbackend.library.*
import com.slender.forumbackend.library.RequireToken.notRequireToken
import com.slender.forumbackend.security.filter.JwtFilter
import com.slender.forumbackend.security.filter.PasswordFilter
import com.slender.forumbackend.security.handler.AccessRefuseHandler
import com.slender.forumbackend.security.handler.ExceptionHandler
import com.slender.forumbackend.security.handler.SignOutHandler
import com.slender.forumbackend.security.handler.SignOutSuccessHandler
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
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
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
