package com.slender.forumbackend.model.cache

data class LoginDataCache(
    val uid: Long,
    val userName: String,
    val email: String,
    val avatar: String,
    val authorities: Set<String> = emptySet(),
){
    fun toUserCache() = UserCache(
        uid = uid,
        name = userName,
        email = email,
        avatar = avatar,
        authorities = authorities,
    )
}
