package com.slender.forumbackend.constant.enumeration.user

import com.baomidou.mybatisplus.annotation.EnumValue

enum class Gender(
    @EnumValue
    val value: String,
) {
    Male("Male"),
    Female("Female"),
    Unknown("Unknown"),
}