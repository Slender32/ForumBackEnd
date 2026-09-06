package com.slender.forumbackend.component.user

import com.slender.forumbackend.constant.enumeration.user.UserProfileTagType.*
import com.slender.forumbackend.constant.enumeration.user.UserStatus.*
import com.slender.forumbackend.model.data.user.UserProfileTagData
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.repository.RbacRepository
import org.springframework.stereotype.Component

@Component
class UserProfileTagAssembler(
    private val rbacRepository: RbacRepository,
) {
    fun assemble(user: User): List<UserProfileTagData> {
        val roles = rbacRepository.findEnabledRolesByUserId(user.uid)
        val roleTag = when {
            roles.any { it.code == "ADMIN" } -> UserProfileTagData("管理员", Role)
            roles.any { it.code == "MODERATOR" } -> UserProfileTagData("版主", Role)
            else -> UserProfileTagData("普通用户", Role)
        }
        val statusTag = when (user.status) {
            BANNED -> UserProfileTagData("封禁", Status)
            DELETED -> UserProfileTagData("已注销",Status)
            ACTIVE -> UserProfileTagData("正常", Status)
        }
        return listOf(roleTag, statusTag)
    }
}
