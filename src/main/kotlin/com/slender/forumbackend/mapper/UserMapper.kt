package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.constant.field.UserField.EMAIL
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.entity.user.content.UserStatistics
import com.slender.forumbackend.model.entity.user.relation.UserFollow
import com.slender.forumbackend.model.entity.user.relation.UserTag
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
interface UserMapper : BaseMapper<User>

@Mapper
interface UserStatisticsMapper : BaseMapper<UserStatistics>

@Mapper
interface UserFollowMapper : BaseMapper<UserFollow>

@Mapper
interface UserTagMapper : BaseMapper<UserTag>

@Repository
class UserRepository(
    private val userMapper: UserMapper,
    private val userStatisticsMapper: UserStatisticsMapper,
    private val userFollowMapper: UserFollowMapper,
    private val userTagMapper: UserTagMapper,
) {
    fun findByEmail(email: String): User? =
        userMapper.selectOne(QueryWrapper<User>().eq(EMAIL, email))

    fun findStatisticsById(uid: Long): UserStatistics? =
        userStatisticsMapper.selectById(uid)

    fun findByIds(userIds: Collection<Long>) =
        userIds.distinct().takeIf { it.isNotEmpty() }?.let { userMapper.selectByIds(it) } ?: emptyList()

    fun createUser(user: User): Long {
        userMapper.insert(user)
        return user.uid
    }

    fun createStatistics(uid: Long) {
        userStatisticsMapper.insert(UserStatistics(uid))
    }
}
