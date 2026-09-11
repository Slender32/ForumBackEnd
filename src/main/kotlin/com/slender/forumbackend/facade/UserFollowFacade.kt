package com.slender.forumbackend.facade

import com.slender.forumbackend.service.user.UserFollowQueryService
import org.springframework.stereotype.Service

@Service
class UserFollowFacade(
    private val service: UserFollowQueryService
) {
    fun following(uid: Long, page: Int, size: Int) = service.following(uid, page, size)
    fun followers(uid: Long, page: Int, size: Int) = service.followers(uid, page, size)
}
