package com.slender.forumbackend.constant.enumeration.notice

import com.baomidou.mybatisplus.annotation.EnumValue

enum class CommentNoticeType(
    @EnumValue
    val value: String,
) {
    COMMENT("COMMENT"),
    REPLY("REPLY"),
}
