package com.slender.forumbackend.model.data.user

import com.slender.forumbackend.constant.enumeration.user.UserProfileTagType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户主页资料")
data class UserProfileData(
    val uid: Long,
    val name: String,
    val avatar: String,
    val signature: String,
    val createTime: Long,
    val tags: List<UserProfileTagData>,
    val stats: UserProfileStatsData,
    val isFollowing: Boolean = false,
    val isSelf: Boolean = false,
)

@Schema(description = "用户主页标签")
data class UserProfileTagData(
    val name: String,
    val type: UserProfileTagType,
)

@Schema(description = "用户主页统计")
data class UserProfileStatsData(
    val moePoint: Long = 0,
    val articleCount: Int = 0,
    val likedCount: Int = 0,
    val promotedCount: Int = 0,
    val fanCount: Int = 0,
    val followCount: Int = 0,
)

@Schema(description = "关注切换结果")
data class FollowToggleData(
    val isFollowing: Boolean,
    val fanCount: Int,
)
