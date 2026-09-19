package com.slender.forumbackend.library

import jakarta.servlet.Filter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.web.cors.CorsConfiguration

internal inline fun HttpSecurity.appendFilter(block: HttpSecurity.() -> Unit) = block()

internal inline fun <reified T: Filter> HttpSecurity.before(filter: Filter) =
    addFilterBefore(filter,T::class.java)

internal inline fun <reified T: Filter> HttpSecurity.after(filter: Filter) =
    addFilterAfter(filter,T::class.java)

internal inline fun <reified T: Filter> HttpSecurity.at(filter: Filter) =
    addFilterAt(filter,T::class.java)

internal fun HttpSecurity.corsConfiguration(block: CorsConfiguration.() -> Unit) =
    cors { it.configurationSource { CorsConfiguration().apply(block) } }

internal fun HttpSecurity.noAuthentication(vararg paths: String){
    authorizeHttpRequests { it.apply {
        requestMatchers(
            *paths
        ).permitAll()
        anyRequest().authenticated()
    } }
}

internal fun HttpSecurity.authentication(authority: String, vararg paths: String){
    authorizeHttpRequests { it.requestMatchers(*paths).hasAuthority(authority) }
}

internal inline fun HttpSecurity.signOut(
    crossinline block: LogoutConfigurer<HttpSecurity>.() -> Unit
) = logout { it.block() }

internal inline fun HttpSecurity.exceptionHandler(
    crossinline block: ExceptionHandlingConfigurer<HttpSecurity>.() -> Unit
) = exceptionHandling { it.block() }

internal fun HttpSecurity.noSession() = sessionManagement { it.sessionCreationPolicy(STATELESS) }

internal fun HttpSecurity.disableCsrf() = csrf { it.disable() }

internal inline fun HttpSecurity.configure(block: HttpSecurity.() -> Unit) = apply(block).build()
