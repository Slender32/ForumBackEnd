package com.slender.forumbackend.service.user

import com.slender.forumbackend.model.data.PageData
import com.slender.forumbackend.model.data.user.UserProfileData
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Service

@Service
class UserFollowQueryService(
    private val users: UserReadRepository,
    private val profiles: UserProfileService,
) {
    fun following(uid: Long, page: Int, size: Int): PageData<UserProfileData> {
        users.findByIdOrThrow(uid)
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return page(
            safePage,
            safeSize,
            users.countFollowing(uid),
            users.listFollowingIds(uid, (safePage - 1) * safeSize, safeSize),
            uid,
        )
    }

    fun followers(uid: Long, page: Int, size: Int): PageData<UserProfileData> {
        users.findByIdOrThrow(uid)
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return page(
            safePage,
            safeSize,
            users.countFollowers(uid),
            users.listFollowerIds(uid, (safePage - 1) * safeSize, safeSize),
            uid,
        )
    }

    private fun page(page: Int, size: Int, total: Long, ids: List<Long>, relationOwnerId: Long) =
        PageData(
            items =
                ids.mapNotNull {
                    runCatching { profiles.profile(it, relationOwnerId) }.getOrNull()
                },
            page = page,
            size = size,
            total = total,
        )
}
