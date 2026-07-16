package com.slender.forumbackend.model.cache

data class UserCache(
    val uid: Long,
    val name: String,
    val email: String,
    val avatar: String,
    val authorities: Set<String>
)
