package com.slender.forumbackend.constant.enumeration.article

import com.baomidou.mybatisplus.annotation.EnumValue

enum class ArticleVisibility(
    @EnumValue
    val value: String,
) {
    Public("PUBLIC"),
    Private("PRIVATE"),
    Followers("FOLLOWERS"),
}
