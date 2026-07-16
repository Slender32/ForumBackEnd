package com.slender.forumbackend.constant.util

import com.slender.forumbackend.constant.core.Jwt.ACCESS_KEY
import com.slender.forumbackend.constant.core.Jwt.ACCESS_TOKEN_EXPIRATION_TIME
import com.slender.forumbackend.constant.core.Jwt.REFRESH_KEY
import com.slender.forumbackend.constant.core.Jwt.REFRESH_TOKEN_EXPIRATION_TIME
import com.slender.forumbackend.constant.field.UserField.UID
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys.hmacShaKeyFor
import java.lang.System.currentTimeMillis
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Date

object JwtToolkit {
    private fun getToken(
        key: String,
        expirationTime: Long,
        data: Map<String, Any>
    ): String = Jwts.builder()
        .signWith(hmacShaKeyFor(key.toByteArray(UTF_8)))
        .claims(data)
        .expiration(Date(currentTimeMillis() + expirationTime))
        .compact()

    fun accessToken(uid: Long): String =
        getToken(ACCESS_KEY, ACCESS_TOKEN_EXPIRATION_TIME, mapOf(UID to uid))

    fun refreshToken(uid: Long): String =
        getToken(REFRESH_KEY, REFRESH_TOKEN_EXPIRATION_TIME, mapOf(UID to uid))

    fun parseToken(key: String, userToken: String): Map<String, Any> =
        Jwts.parser()
            .verifyWith(hmacShaKeyFor(key.toByteArray(UTF_8)))
            .build()
            .parseSignedClaims(userToken)
            .payload
}
