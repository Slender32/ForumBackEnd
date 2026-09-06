package com.slender.forumbackend.constant.enumeration.carousel

import com.baomidou.mybatisplus.annotation.EnumValue

enum class CarouselTargetType(
    @EnumValue
    val value: String,
) {
    Article("Article"),
    Url("Url"),
    None("None"),
}
