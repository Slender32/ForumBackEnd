package com.slender.forumbackend.service.auth

import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Service

@Service
class CurrentUserQueryService(private val users: UserReadRepository) {
    fun current(uid: Long): UserData = users.findActiveByIdOrThrow(uid).toUserData(users.findStatisticsById(uid))
}
