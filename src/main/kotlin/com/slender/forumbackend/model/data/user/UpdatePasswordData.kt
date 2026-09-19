package com.slender.forumbackend.model.data.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "修改密码结果")
data class UpdatePasswordData(
    @field:Schema(description = "是否已使账号原有登录凭证失效")
    val tokenInvalidated: Boolean,
)
