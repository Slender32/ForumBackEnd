package com.slender.forumbackend.service.user

import com.slender.forumbackend.configuration.BUSINESS_ZONE
import com.slender.forumbackend.exception.AlreadyCheckedInException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.data.user.*
import com.slender.forumbackend.repository.user.UserPointsRepository
import org.springframework.stereotype.Service
import java.time.Clock
import kotlin.random.Random

@Service
class UserPointsService(private val repository: UserPointsRepository, private val clock: Clock) {
    fun points(userId: Long, page: Int, size: Int): UserPointsData {
        if (page < 0 || size !in 1..100) throw InvalidRequestException("page >= 0, size in 1..100 required")

        val now = clock.instant().atZone(BUSINESS_ZONE)
        val today = now.toLocalDate()
        val current = repository.checkIn(userId, today)
        val previous = current ?: repository.checkIn(userId, today.minusDays(1))
        val records = repository.records(userId, size + 1, page.toLong() * size)

        return UserPointsData(
            repository.balance(userId),
            records.take(size).map {
                UserPointRecordData(
                    it.recordId, it.amount, it.balance, it.reason,
                    it.createTime.atZone(BUSINESS_ZONE).toInstant().toEpochMilli()
                )
            },
            page, size, records.size > size,
            CheckInStatusData(
                current != null,
                previous?.consecutiveDays ?: 0,
                previous?.checkInDate?.toString(),
                today.plusDays(1).atStartOfDay(BUSINESS_ZONE).toInstant().toEpochMilli()
            ),
        )
    }

    fun checkIn(userId: Long): CheckInData {
        repository.lockBalance(userId)
        val now = clock.instant().atZone(BUSINESS_ZONE)
        val today = now.toLocalDate()
        if (repository.checkIn(userId, today) != null) throw AlreadyCheckedInException()

        val streak = (repository.checkIn(userId, today.minusDays(1))?.consecutiveDays ?: 0) + 1
        val points = Random.nextInt(5, 21)
        val balance = repository.add(userId, points.toLong())
        repository.insertCheckIn(userId, today, points, streak, now.toLocalDateTime())
        repository.record(userId, points.toLong(), balance, "CHECK_IN", now.toLocalDateTime(), "check-in:$today")

        return CheckInData(
            points, balance, streak, today.toString(),
            today.plusDays(1).atStartOfDay(BUSINESS_ZONE).toInstant().toEpochMilli()
        )
    }
}
