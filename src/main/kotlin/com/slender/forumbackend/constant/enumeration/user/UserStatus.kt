package com.slender.forumbackend.constant.enumeration.user

import com.baomidou.mybatisplus.annotation.EnumValue

enum class UserStatus(
    @EnumValue
    val value: String,
) {
    ACTIVE("ACTIVE"),
    BANNED("BANNED"),
    DELETED("DELETED"),
}
