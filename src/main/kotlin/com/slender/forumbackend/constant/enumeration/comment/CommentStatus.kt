package com.slender.forumbackend.constant.enumeration.comment

import com.baomidou.mybatisplus.annotation.EnumValue

enum class CommentStatus(
    @EnumValue
    val value: String,
) {
    Normal("NORMAL"),
    Deleted("DELETED"),
}
