package com.slender.forumbackend.model.data

import com.slender.forumbackend.model.token.IssuedToken
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Token刷新响应数据")
data class RefreshData(
    @field:Schema(description = "新的访问令牌", example = "eyJhbGciOiJIUzI1NiJ9.access")
    val accessToken: String,

    @field:Schema(description = "新的刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9.refresh")
    val refreshToken: String,

    @field:Schema(description = "访问令牌过期时间戳，单位毫秒", example = "1767229200000")
    val accessTokenExpireAt: Long,

    @field:Schema(description = "刷新令牌过期时间戳，单位毫秒", example = "1767484800000")
    val refreshTokenExpireAt: Long,

    @field:Schema(description = "当前登录用户信息")
    val userData: UserData,
) {
    constructor(
        accessToken: IssuedToken,
        refreshToken: IssuedToken,
        userData: UserData
    ): this(
        accessToken = accessToken.value,
        refreshToken = refreshToken.value,
        accessTokenExpireAt = accessToken.expireAt,
        refreshTokenExpireAt = refreshToken.expireAt,
        userData = userData,
    )
}
