package com.slender.forumbackend.model.token

/**
 * 一次签发的JWT及其绝对过期时间(毫秒时间戳)，用于向前端同步 `expireAt`。
 */
data class IssuedToken(
    val value: String,
    val expireAt: Long,
)
