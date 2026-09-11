package com.slender.forumbackend.model.data

data class AdminPageData<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Long
)
