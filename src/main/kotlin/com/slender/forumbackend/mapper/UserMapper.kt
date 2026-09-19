package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.entity.user.content.UserStatistics
import com.slender.forumbackend.model.entity.user.relation.UserFollow
import com.slender.forumbackend.model.entity.user.relation.UserTag
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper : BaseMapper<User>

@Mapper
interface UserStatisticsMapper : BaseMapper<UserStatistics> {
    fun addPublishedArticleCount(
        @Param("userId") userId: Long,
        @Param("delta") delta: Int
    ): Int

    fun deductMoePoint(
        @Param("userId") userId: Long,
        @Param("amount") amount: Long
    ): Int

    fun addMoePoint(
        @Param("userId") userId: Long,
        @Param("amount") amount: Long
    ): Int

    fun addFollowCount(
        @Param("userId") userId: Long,
        @Param("delta") delta: Int
    ): Int

    fun addFanCount(
        @Param("userId") userId: Long,
        @Param("delta") delta: Int
    ): Int
}

@Mapper
interface UserFollowMapper : BaseMapper<UserFollow>

@Mapper
interface UserTagMapper : BaseMapper<UserTag>
