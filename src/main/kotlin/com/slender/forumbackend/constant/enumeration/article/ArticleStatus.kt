package com.slender.forumbackend.constant.enumeration.article

import com.baomidou.mybatisplus.annotation.EnumValue

enum class ArticleStatus(
    @EnumValue
    val value: String,
) {
    Draft("DRAFT"),
    Published("PUBLISHED"),
    Deleted("DELETED"),
}
