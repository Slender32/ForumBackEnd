package com.slender.forumbackend.model.data.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "积分余额、流水及签到状态")
data class UserPointsData(
    @field:Schema(description = "萌萌点余额")
    val balance: Long,
    @field:Schema(description = "当前页数据列表")
    val items: List<UserPointRecordData>,
    @field:Schema(description = "当前页码，从 0 开始")
    val page: Int,
    @field:Schema(description = "每页数量")
    val size: Int,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
    @field:Schema(description = "签到状态")
    val checkIn: CheckInStatusData,
)

@Schema(description = "积分变动流水")
data class UserPointRecordData(
    @field:Schema(description = "记录 ID")
    val id: Long,
    @field:Schema(description = "本次积分变动数量，正数为收入、负数为支出")
    val amount: Long,
    @field:Schema(description = "该笔变动后的萌萌点余额")
    val balance: Long,
    @field:Schema(description = "积分变动原因")
    val reason: String,
    @field:Schema(description = "创建时间，Unix 毫秒时间戳")
    val createTime: Long,
)

@Schema(description = "当前用户签到状态")
data class CheckInStatusData(
    @field:Schema(description = "今天是否已签到")
    val checkedInToday: Boolean,
    @field:Schema(description = "连续签到天数")
    val consecutiveDays: Int,
    @field:Schema(description = "最近签到日期，格式 yyyy-MM-dd，业务时区 Asia/Shanghai；从未签到时为 null")
    val checkInDate: String?,
    @field:Schema(description = "下次可签到时间，Unix 毫秒时间戳")
    val nextCheckInTime: Long,
)

@Schema(description = "每日签到结果")
data class CheckInData(
    @field:Schema(description = "本次签到获得的萌萌点")
    val points: Int,
    @field:Schema(description = "萌萌点余额")
    val balance: Long,
    @field:Schema(description = "连续签到天数")
    val consecutiveDays: Int,
    @field:Schema(description = "签到日期，格式 yyyy-MM-dd，业务时区 Asia/Shanghai")
    val checkInDate: String,
    @field:Schema(description = "下次可签到时间，Unix 毫秒时间戳")
    val nextCheckInTime: Long,
)
