package com.slender.forumbackend.constant.enumeration.report

import com.baomidou.mybatisplus.annotation.EnumValue

enum class ReportTargetType(
    @EnumValue val value: String
) {
    Article("ARTICLE"),
    Comment("COMMENT"),
    User("USER"),
}
