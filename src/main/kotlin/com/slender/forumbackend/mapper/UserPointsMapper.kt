package com.slender.forumbackend.mapper

import com.slender.forumbackend.model.entity.user.content.CheckInRecord
import com.slender.forumbackend.model.entity.user.content.PointRecord
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDate
import java.time.LocalDateTime

@Mapper
interface UserPointsMapper {
    fun balance(
        @Param("userId") userId: Long
    ): Long?

    fun lockBalance(
        @Param("userId") userId: Long
    ): Long?

    fun records(
        @Param("userId") userId: Long,
        @Param("limit") limit: Int,
        @Param("offset") offset: Long
    ): List<PointRecord>

    fun checkIn(
        @Param("userId") userId: Long,
        @Param("date") date: LocalDate
    ): CheckInRecord?

    fun insertCheckIn(
        @Param("userId") userId: Long,
        @Param("date") date: LocalDate,
        @Param("points") points: Int,
        @Param("streak") streak: Int,
        @Param("now") now: LocalDateTime
    ): Int

    fun insert(
        @Param("userId") userId: Long,
        @Param("amount") amount: Long,
        @Param("balance") balance: Long,
        @Param("reason") reason: String,
        @Param("now") now: LocalDateTime,
        @Param("key") key: String
    ): Int

    fun add(
        @Param("userId") userId: Long,
        @Param("amount") amount: Long
    ): Int
}
