package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Token刷新响应数据")
data class RefreshData(
    @field:Schema(description = "用户ID", example = "1")
    val uid: Long,

    @field:Schema(description = "用户名", example = "slender")
    val name: String,

    @field:Schema(description = "新的访问令牌", example = "eyJhbGciOiJIUzI1NiJ9.access")
    val accessToken: String,

    @field:Schema(description = "新的刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9.refresh")
    val refreshToken: String
)
