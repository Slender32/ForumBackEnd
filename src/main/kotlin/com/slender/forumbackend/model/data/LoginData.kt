package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "登录成功响应数据")
data class LoginData(
    @field:Schema(description = "用户ID", example = "1")
    val uid: Long,

    @field:Schema(description = "用户名", example = "slender")
    val userName: String,

    @field:Schema(description = "访问令牌，用于访问需要登录的接口", example = "eyJhbGciOiJIUzI1NiJ9.access")
    val accessToken: String,

    @field:Schema(description = "刷新令牌，用于刷新登录状态", example = "eyJhbGciOiJIUzI1NiJ9.refresh")
    val refreshToken: String,

    @field:Schema(description = "当前登录用户信息")
    val userData: UserData,
)
