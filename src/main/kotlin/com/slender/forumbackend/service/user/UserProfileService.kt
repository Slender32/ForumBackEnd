package com.slender.forumbackend.service.user

import com.slender.forumbackend.component.user.UserDataAssembler
import com.slender.forumbackend.component.user.UserProfileTagAssembler
import com.slender.forumbackend.constant.enumeration.user.UserStatus.DELETED
import com.slender.forumbackend.model.data.user.UserProfileData
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val userReadRepository: UserReadRepository,
    private val userDataAssembler: UserDataAssembler,
    private val userProfileTagAssembler: UserProfileTagAssembler,
) {
    fun profile(uid: Long, currentUserId: Long?): UserProfileData {
        val user = userReadRepository.findByIdOrThrow(uid)
        if (user.status == DELETED) return userDataAssembler.deletedProfile(user, currentUserId)
        val isSelf = currentUserId == uid
        val isFollowing = !isSelf && currentUserId != null &&
            userReadRepository.findFollow(currentUserId, uid) != null
        return userDataAssembler.profile(
            user = user,
            statistics = userReadRepository.findStatisticsById(uid),
            tags = userProfileTagAssembler.assemble(user),
            isFollowing = isFollowing,
            isSelf = isSelf,
        )
    }
}
