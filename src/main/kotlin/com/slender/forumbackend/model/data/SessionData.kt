package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "会话数据")
data class SessionData(
    @field:Schema(description = "访问令牌")
    val accessToken: String,
    @field:Schema(description = "刷新令牌")
    val refreshToken: String,
    @field:Schema(description = "访问令牌过期时间戳，单位毫秒")
    val accessTokenExpireAt: Long,
    @field:Schema(description = "刷新令牌过期时间戳，单位毫秒")
    val refreshTokenExpireAt: Long,
    @field:Schema(description = "当前登录用户信息")
    val userData: UserData,
)
