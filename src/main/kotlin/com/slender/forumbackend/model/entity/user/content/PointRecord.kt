package com.slender.forumbackend.model.entity.user.content

import java.time.LocalDate
import java.time.LocalDateTime

data class PointRecord(
    val recordId: Long,
    val amount: Long,
    val balance: Long,
    val reason: String,
    val createTime: LocalDateTime,
)

data class CheckInRecord(
    val checkInDate: LocalDate,
    val points: Int,
    val consecutiveDays: Int,
)
