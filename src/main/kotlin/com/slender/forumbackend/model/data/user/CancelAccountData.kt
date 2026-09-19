package com.slender.forumbackend.model.data.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "注销账号结果")
data class CancelAccountData(
    @field:Schema(description = "用户 ID")
    val uid: Long,
    @field:Schema(description = "账号注销时间，Unix 毫秒时间戳")
    val cancelledAt: Long,
    @field:Schema(description = "是否已使账号原有登录凭证失效")
    val tokenInvalidated: Boolean = true,
)
