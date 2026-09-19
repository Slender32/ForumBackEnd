package com.slender.forumbackend.model.data.user

import com.slender.forumbackend.constant.enumeration.user.UserProfileTagType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户主页资料")
data class UserProfileData(
    @field:Schema(description = "用户 ID")
    val uid: Long,
    @field:Schema(description = "名称")
    val name: String,
    @field:Schema(description = "头像访问地址")
    val avatar: String,
    @field:Schema(description = "个性签名")
    val signature: String,
    @field:Schema(description = "创建时间，Unix 毫秒时间戳")
    val createTime: Long,
    @field:Schema(description = "标签列表")
    val tags: List<UserProfileTagData>,
    @field:Schema(description = "用户统计信息")
    val stats: UserProfileStatsData,
    @field:Schema(description = "当前用户是否已关注该用户")
    @get:Schema(description = "当前用户是否已关注该用户")
    val isFollowing: Boolean = false,
    @field:Schema(description = "是否为当前登录用户本人")
    @get:Schema(description = "是否为当前登录用户本人")
    val isSelf: Boolean = false,
)

@Schema(description = "用户主页标签")
data class UserProfileTagData(
    @field:Schema(description = "名称")
    val name: String,
    @field:Schema(description = "用户标签类型")
    val type: UserProfileTagType,
)

@Schema(description = "用户主页统计")
data class UserProfileStatsData(
    @field:Schema(description = "萌萌点余额")
    val moePoint: Long = 0,
    @field:Schema(description = "文章数量")
    val articleCount: Int = 0,
    @field:Schema(description = "获赞数量")
    val likedCount: Int = 0,
    @field:Schema(description = "被推荐数量")
    val promotedCount: Int = 0,
    @field:Schema(description = "粉丝数量")
    val fanCount: Int = 0,
    @field:Schema(description = "关注数量")
    val followCount: Int = 0,
)

@Schema(description = "关注切换结果")
data class FollowToggleData(
    @field:Schema(description = "当前用户是否已关注该用户")
    @get:Schema(description = "当前用户是否已关注该用户")
    val isFollowing: Boolean,
    @field:Schema(description = "粉丝数量")
    val fanCount: Int,
)
