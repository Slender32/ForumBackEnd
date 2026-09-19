package com.slender.forumbackend.repository.user

import com.slender.forumbackend.exception.AlreadyCheckedInException
import com.slender.forumbackend.mapper.UserPointsMapper
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class UserPointsRepository(private val mapper: UserPointsMapper) {
    fun balance(userId: Long): Long = checkNotNull(mapper.balance(userId)) { "Missing user statistics: $userId" }

    fun lockBalance(userId: Long): Long = checkNotNull(mapper.lockBalance(userId)) { "Missing user statistics: $userId" }

    fun lockBalances(userIds: Collection<Long>) = userIds.distinct().sorted().associateWith(::lockBalance)

    fun records(userId: Long, limit: Int, offset: Long) = mapper.records(userId, limit, offset)

    fun checkIn(userId: Long, date: LocalDate) = mapper.checkIn(userId, date)

    fun insertCheckIn(userId: Long, date: LocalDate, points: Int, streak: Int, now: LocalDateTime) {
        if (mapper.insertCheckIn(userId, date, points, streak, now) != 1) throw AlreadyCheckedInException()
    }

    fun add(userId: Long, amount: Long): Long {
        check(mapper.add(userId, amount) == 1) { "Point balance update failed: $userId" }
        return balance(userId)
    }

    fun record(userId: Long, amount: Long, balance: Long, reason: String, now: LocalDateTime, key: String) {
        check(mapper.insert(userId, amount, balance, reason, now, key) == 1)
    }
}
