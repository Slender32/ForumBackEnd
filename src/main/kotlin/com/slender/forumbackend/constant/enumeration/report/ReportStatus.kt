package com.slender.forumbackend.constant.enumeration.report

import com.baomidou.mybatisplus.annotation.EnumValue

enum class ReportStatus(
    @EnumValue val value: String
) {
    Pending("PENDING"),
    Resolved("RESOLVED"),
    Rejected("REJECTED"),
}
