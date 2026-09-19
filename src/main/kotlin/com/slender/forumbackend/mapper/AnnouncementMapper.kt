package com.slender.forumbackend.mapper

import com.slender.forumbackend.model.entity.announcement.Announcement
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

@Mapper
interface AnnouncementMapper {
    fun list(
        @Param("placement") placement: String,
        @Param("limit") limit: Int,
        @Param("userId") userId: Long?,
        @Param("now") now: LocalDateTime
    ): List<Announcement>

    fun unreadCount(
        @Param("placement") placement: String,
        @Param("userId") userId: Long,
        @Param("now") now: LocalDateTime
    ): Long

    fun lockVisible(
        @Param("id") id: Long,
        @Param("now") now: LocalDateTime
    ): Long?

    fun read(
        @Param("id") id: Long,
        @Param("userId") userId: Long,
        @Param("now") now: LocalDateTime
    ): Int
}
