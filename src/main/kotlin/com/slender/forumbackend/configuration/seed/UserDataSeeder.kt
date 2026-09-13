package com.slender.forumbackend.configuration.seed

import com.slender.forumbackend.constant.enumeration.user.Gender
import com.slender.forumbackend.constant.enumeration.user.UserStatus
import com.slender.forumbackend.mapper.UserMapper
import com.slender.forumbackend.mapper.UserRoleMapper
import com.slender.forumbackend.mapper.UserStatisticsMapper
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.entity.user.content.UserStatistics
import com.slender.forumbackend.model.entity.user.relation.UserRole
import com.slender.forumbackend.repository.RbacRepository
import java.time.LocalDateTime
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Profile("dev")
@Component
class UserDataSeeder(
    private val passwordEncoder: PasswordEncoder,
    private val rbacRepository: RbacRepository,
    private val userMapper: UserMapper,
    private val userStatisticsMapper: UserStatisticsMapper,
    private val userRoleMapper: UserRoleMapper,
) {
    fun seed(now: LocalDateTime): SeedUserIds {
        val roleIds = findRoleIds()
        return seedUsers(now, roleIds)
    }

    private fun seedUsers(now: LocalDateTime, roleIds: RoleIds): SeedUserIds {
        val adminId =
            seedUser(
                name = "demo-admin",
                email = "admin.demo@forum.test",
                avatar = "https://example.com/avatar-admin.png",
                level = 6,
                gender = Gender.Unknown,
                signature = "Keeps the demo alive",
                password = "Demo1234!",
                roleId = roleIds.adminRoleId,
                statistics =
                    UserStatisticSeed(
                        fanCount = 12,
                        followCount = 4,
                        publishedArticleCount = 1,
                        likedCount = 8,
                        moePoint = 999,
                        promotedCount = 2,
                    ),
                now = now,
            )
        val aliceId =
            seedUser(
                name = "alice.writer",
                email = "alice.demo@forum.test",
                avatar = "https://example.com/avatar-alice.png",
                level = 4,
                gender = Gender.Female,
                signature = "Writes the feed chain notes",
                password = "Demo1234!",
                roleId = roleIds.adminRoleId,
                statistics =
                    UserStatisticSeed(
                        fanCount = 35,
                        followCount = 18,
                        publishedArticleCount = 2,
                        likedCount = 14,
                        moePoint = 120,
                        promotedCount = 3,
                    ),
                now = now,
            )
        val bobId =
            seedUser(
                name = "bob.reviewer",
                email = "bob.demo@forum.test",
                avatar = "https://example.com/avatar-bob.png",
                level = 3,
                gender = Gender.Male,
                signature = "Keeps the review path honest",
                password = "Demo1234!",
                roleId = roleIds.userRoleId,
                statistics =
                    UserStatisticSeed(
                        fanCount = 21,
                        followCount = 9,
                        publishedArticleCount = 1,
                        likedCount = 11,
                        moePoint = 75,
                        promotedCount = 1,
                    ),
                now = now,
            )
        val carolId =
            seedUser(
                name = "carol.design",
                email = "carol.demo@forum.test",
                avatar = "https://example.com/avatar-carol.png",
                level = 2,
                gender = Gender.Female,
                signature = "Cares about the first screen",
                password = "Demo1234!",
                roleId = roleIds.userRoleId,
                statistics =
                    UserStatisticSeed(
                        fanCount = 8,
                        followCount = 14,
                        publishedArticleCount = 1,
                        likedCount = 6,
                        moePoint = 34,
                        promotedCount = 1,
                    ),
                now = now,
            )
        val daveId =
            seedUser(
                name = "dave.reader",
                email = "dave.demo@forum.test",
                avatar = "https://example.com/avatar-dave.png",
                level = 1,
                gender = Gender.Male,
                signature = "Useful for login and like toggles",
                password = "Demo1234!",
                roleId = roleIds.userRoleId,
                statistics =
                    UserStatisticSeed(
                        fanCount = 3,
                        followCount = 27,
                        publishedArticleCount = 0,
                        likedCount = 2,
                        moePoint = 20,
                        promotedCount = 0,
                    ),
                now = now,
            )

        return SeedUserIds(adminId, aliceId, bobId, carolId, daveId)
    }

    private fun seedUser(
        name: String,
        email: String,
        avatar: String,
        level: Int,
        gender: Gender,
        signature: String,
        password: String,
        roleId: Long,
        statistics: UserStatisticSeed,
        now: LocalDateTime,
    ): Long {
        val user =
            User(
                name = name,
                email = email,
                passwordHash = passwordEncoder.encode(password)!!,
                avatar = avatar,
                level = level,
                gender = gender,
                signature = signature,
                status = UserStatus.ACTIVE,
                createTime = now,
                updateTime = now,
            )
        userMapper.insert(user)
        val userId = user.uid.requireGeneratedId("user", email)

        userStatisticsMapper.insert(
            UserStatistics(
                userId = userId,
                fanCount = statistics.fanCount,
                followCount = statistics.followCount,
                publishedArticleCount = statistics.publishedArticleCount,
                likedCount = statistics.likedCount,
                moePoint = statistics.moePoint,
                promotedCount = statistics.promotedCount,
            )
        )
        userRoleMapper.insert(UserRole(userId, roleId, now))
        return userId
    }

    private fun findRoleIds(): RoleIds {
        val adminRoleId =
            rbacRepository.findEnabledRoleIdByCode("ADMIN") ?: error("ADMIN role not found")
        val userRoleId =
            rbacRepository.findEnabledRoleIdByCode("USER") ?: error("USER role not found")
        return RoleIds(adminRoleId, userRoleId)
    }

    private fun Long.requireGeneratedId(type: String, label: String): Long {
        require(this > 0) { "DemoDataSeeder failed to get generated $type id for $label" }
        return this
    }

    private data class RoleIds(val adminRoleId: Long, val userRoleId: Long)

    private data class UserStatisticSeed(
        val fanCount: Int,
        val followCount: Int,
        val publishedArticleCount: Int,
        val likedCount: Int,
        val moePoint: Int,
        val promotedCount: Int,
    )
}
