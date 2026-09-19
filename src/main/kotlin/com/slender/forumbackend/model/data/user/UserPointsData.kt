package com.slender.forumbackend.model.data.user

data class UserPointsData(
    val balance: Long,
    val items: List<UserPointRecordData>,
    val page: Int,
    val size: Int,
    val hasMore: Boolean,
    val checkIn: CheckInStatusData,
)

data class UserPointRecordData(
    val id: Long,
    val amount: Long,
    val balance: Long,
    val reason: String,
    val createTime: Long,
)

data class CheckInStatusData(
    val checkedInToday: Boolean,
    val consecutiveDays: Int,
    val checkInDate: String?,
    val nextCheckInTime: Long,
)

data class CheckInData(
    val points: Int,
    val balance: Long,
    val consecutiveDays: Int,
    val checkInDate: String,
    val nextCheckInTime: Long,
)
