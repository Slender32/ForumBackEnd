package com.slender.forumbackend.service.user

import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.data.user.FollowToggleData
import com.slender.forumbackend.model.entity.user.relation.UserFollow
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.repository.user.UserWriteRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class UserFollowService(
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
) {
    fun toggleFollow(targetUid: Long, currentUserId: Long): FollowToggleData {
        if (targetUid == currentUserId || targetUid <= 0) throw InvalidRequestException("不能关注自己")
        userReadRepository.findFollowTargetOrThrow(targetUid)

        val existing = userReadRepository.findFollow(currentUserId, targetUid)
        val following = if (existing == null) {
            userWriteRepository.insertFollow(UserFollow(currentUserId, targetUid, now()))
            userWriteRepository.addFollowCount(currentUserId, 1)
            userWriteRepository.addFanCount(targetUid, 1)
            true
        } else {
            userWriteRepository.deleteFollow(currentUserId, targetUid)
            userWriteRepository.addFollowCount(currentUserId, -1)
            userWriteRepository.addFanCount(targetUid, -1)
            false
        }
        return FollowToggleData(
            isFollowing = following,
            fanCount = userReadRepository.findStatisticsById(targetUid)?.fanCount ?: 0,
        )
    }
}
