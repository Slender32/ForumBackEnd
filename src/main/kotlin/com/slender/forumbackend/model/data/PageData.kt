package com.slender.forumbackend.model.data

data class PageData<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Long
)
