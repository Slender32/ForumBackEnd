package com.slender.forumbackend.repository.user

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
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

    fun insertFollow(follow: UserFollow) {
        userFollowMapper.insert(follow)
    }

    fun addMoePoint(userId: Long, amount: Int) {
        userStatisticsMapper.addMoePoint(userId, amount)
    }

    fun addFollowCount(userId: Long, delta: Int) {
        userStatisticsMapper.addFollowCount(userId, delta)
    }

    fun addFanCount(userId: Long, delta: Int) {
        userStatisticsMapper.addFanCount(userId, delta)
    }


    fun updateUser(user: User) = userMapper.updateById(user) > 0

    fun deductMoePoint(userId: Long, amount: Int) =
        userStatisticsMapper.deductMoePoint(userId, amount) > 0


    fun deleteFollow(followerId: Long, followeeId: Long) =
        userFollowMapper.delete(
            QueryWrapper<UserFollow>()
                .eq(FOLLOWER_ID, followerId)
                .eq(FOLLOWEE_ID, followeeId)
        )
}
