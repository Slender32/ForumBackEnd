package com.slender.forumbackend.repository.user

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.enumeration.user.UserStatus.ACTIVE
import com.slender.forumbackend.constant.field.UserField.EMAIL
import com.slender.forumbackend.constant.field.UserField.FOLLOWEE_ID
import com.slender.forumbackend.constant.field.UserField.FOLLOWER_ID
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.UserNotFoundException
import com.slender.forumbackend.mapper.UserFollowMapper
import com.slender.forumbackend.mapper.UserMapper
import com.slender.forumbackend.mapper.UserStatisticsMapper
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.entity.user.content.UserStatistics
import com.slender.forumbackend.model.entity.user.relation.UserFollow
import org.springframework.stereotype.Repository

@Repository
class UserReadRepository(
    private val userMapper: UserMapper,
    private val userStatisticsMapper: UserStatisticsMapper,
    private val userFollowMapper: UserFollowMapper,
) : ServiceImpl<UserMapper, User>(), IService<User> {
    fun findByEmail(email: String): User? =
        userMapper.selectOne(QueryWrapper<User>().eq(EMAIL, email))

    fun findStatisticsById(uid: Long): UserStatistics? = userStatisticsMapper.selectById(uid)

    fun findByIds(userIds: Collection<Long>): List<User> =
        userIds.distinct().takeIf { it.isNotEmpty() }?.let { userMapper.selectByIds(it) }
            ?: emptyList()

    fun findById(uid: Long): User? = userMapper.selectById(uid)

    fun findByIdOrThrow(uid: Long): User = findById(uid) ?: throw UserNotFoundException()

    fun findActiveByIdOrThrow(uid: Long): User =
        findByIdOrThrow(uid).also { if (it.status != ACTIVE) throw BlockException() }

    fun findFollowTargetOrThrow(uid: Long): User =
        findById(uid)?.takeIf { it.status == ACTIVE } ?: throw UserNotFoundException()

    fun findFollow(followerId: Long, followeeId: Long): UserFollow? =
        userFollowMapper.selectOne(
            QueryWrapper<UserFollow>()
                .eq(FOLLOWER_ID, followerId)
                .eq(FOLLOWEE_ID, followeeId)
                .isNull("deleted_at")
        )

    fun listFollowingIds(uid: Long, offset: Int, limit: Int) =
        userFollowMapper
            .selectList(
                QueryWrapper<UserFollow>()
                    .eq(FOLLOWER_ID, uid)
                    .isNull("deleted_at")
                    .orderByDesc("create_time")
                    .last("LIMIT $limit OFFSET $offset")
            )
            .map { it.followeeId }

    fun countFollowing(uid: Long): Long =
        userFollowMapper.selectCount(
            QueryWrapper<UserFollow>().eq(FOLLOWER_ID, uid).isNull("deleted_at")
        )

    fun listFollowerIds(uid: Long, offset: Int, limit: Int) =
        userFollowMapper
            .selectList(
                QueryWrapper<UserFollow>()
                    .eq(FOLLOWEE_ID, uid)
                    .isNull("deleted_at")
                    .orderByDesc("create_time")
                    .last("LIMIT $limit OFFSET $offset")
            )
            .map { it.followerId }

    fun countFollowers(uid: Long): Long =
        userFollowMapper.selectCount(
            QueryWrapper<UserFollow>().eq(FOLLOWEE_ID, uid).isNull("deleted_at")
        )
}
