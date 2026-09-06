package com.slender.forumbackend.model.data.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "注销账号结果")
data class CancelAccountData(
    val uid: Long,
    val cancelledAt: Long,
    val tokenInvalidated: Boolean = true,
)
