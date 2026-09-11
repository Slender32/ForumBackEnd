package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.UserTagRequest
import com.slender.forumbackend.service.user.UserTagService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserTagFacade(
    private val service: UserTagService
) {
    fun list(userId: Long) = service.list(userId)

    @Transactional
    fun add(userId: Long, request: UserTagRequest, operatorId: Long, authorities: Set<String>) =
        service.add(userId, request, operatorId, authorities)

    @Transactional
    fun delete(userId: Long, tagId: Long, operatorId: Long, authorities: Set<String>) =
        service.delete(userId, tagId, operatorId, authorities)
}
