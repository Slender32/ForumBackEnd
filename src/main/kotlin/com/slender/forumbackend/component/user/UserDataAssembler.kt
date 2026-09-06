package com.slender.forumbackend.component.user

import com.slender.forumbackend.constant.enumeration.user.UserProfileTagType
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.user.UserProfileData
import com.slender.forumbackend.model.data.user.UserProfileStatsData
import com.slender.forumbackend.model.data.user.UserProfileTagData
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.entity.user.content.UserStatistics
import org.springframework.stereotype.Component

@Component
class UserDataAssembler {
    fun deletedProfile(user: User, currentUserId: Long?): UserProfileData =
        UserProfileData(
            uid = user.uid, name = user.name, avatar = "", signature = "", createTime = user.createTime.timestamp,
            tags = listOf(UserProfileTagData("已注销", UserProfileTagType.Status)),
            stats = UserProfileStatsData(0, 0, 0, 0, 0, 0),
            isFollowing = false, isSelf = currentUserId == user.uid,
        )

    fun profile(user: User, statistics: UserStatistics?, tags: List<UserProfileTagData>, isFollowing: Boolean, isSelf: Boolean): UserProfileData =
        UserProfileData(
            uid = user.uid, name = user.name, avatar = user.avatar, signature = user.signature,
            createTime = user.createTime.timestamp, tags = tags,
            stats = statistics?.let {
                UserProfileStatsData(
                    moePoint = it.moePoint,
                    articleCount = it.publishedArticleCount,
                    likedCount = it.likedCount,
                    promotedCount = it.promotedCount,
                    fanCount = it.fanCount,
                    followCount = it.followCount,
                )
            } ?: UserProfileStatsData(),
            isFollowing = isFollowing, isSelf = isSelf,
        )
}
