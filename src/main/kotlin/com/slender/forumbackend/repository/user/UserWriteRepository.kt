package com.slender.forumbackend.repository.user

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.UserField.FOLLOWEE_ID
import com.slender.forumbackend.constant.field.UserField.FOLLOWER_ID
import com.slender.forumbackend.mapper.UserFollowMapper
import com.slender.forumbackend.mapper.UserMapper
import com.slender.forumbackend.mapper.UserStatisticsMapper
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.entity.user.content.UserStatistics
import com.slender.forumbackend.model.entity.user.relation.UserFollow
import org.springframework.stereotype.Repository

@Repository
class UserWriteRepository(
    private val userMapper: UserMapper,
    private val userStatisticsMapper: UserStatisticsMapper,
    private val userFollowMapper: UserFollowMapper,
) : ServiceImpl<UserMapper, User>(), IService<User> {

    fun createUser(user: User): Long {
        userMapper.insert(user)
        return user.uid
    }

    fun createStatistics(uid: Long) {
        userStatisticsMapper.insert(UserStatistics(uid))
    }

    fun addPublishedArticleCount(userId: Long, delta: Int) {
        check(userStatisticsMapper.addPublishedArticleCount(userId, delta) == 1) {
            "Missing user statistics: $userId"
        }
    }

    fun insertFollow(follow: UserFollow) {
        val restored =
            userFollowMapper.update(
                null,
                UpdateWrapper<UserFollow>()
                    .eq(FOLLOWER_ID, follow.followerId)
                    .eq(FOLLOWEE_ID, follow.followeeId)
                    .isNotNull("deleted_at")
                    .set("deleted_at", null)
                    .set("create_time", follow.createTime),
            )
        if (restored > 0) return
        userFollowMapper.insert(follow)
    }

    fun addMoePoint(userId: Long, amount: Long) {
        check(userStatisticsMapper.addMoePoint(userId, amount) == 1) { "Missing user statistics: $userId" }
    }

    fun addFollowCount(userId: Long, delta: Int) {
        userStatisticsMapper.addFollowCount(userId, delta)
    }

    fun addFanCount(userId: Long, delta: Int) {
        userStatisticsMapper.addFanCount(userId, delta)
    }

    fun updateUser(user: User) = userMapper.updateById(user) > 0

    fun updateSignature(uid: Long, signature: String, updateTime: java.time.LocalDateTime): Boolean {
        val current = userMapper.selectById(uid) ?: return false
        if (current.status != com.slender.forumbackend.constant.enumeration.user.UserStatus.ACTIVE) return false
        return userMapper.updateById(current.copy(signature = signature, updateTime = updateTime)) > 0
    }

    fun deleteFollow(followerId: Long, followeeId: Long) =
        userFollowMapper.update(
            null,
            UpdateWrapper<UserFollow>()
                .eq(FOLLOWER_ID, followerId)
                .eq(FOLLOWEE_ID, followeeId)
                .isNull("deleted_at")
                .set("deleted_at", java.time.LocalDateTime.now()),
        )
}
