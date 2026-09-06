package com.slender.forumbackend.service.auth

import com.slender.forumbackend.component.user.RegisterCaptchaValidator
import com.slender.forumbackend.component.user.UserFactory
import com.slender.forumbackend.constant.core.Redis.Key.REGISTER_CAPTCHA
import com.slender.forumbackend.exception.UserAlreadyExistsException
import com.slender.forumbackend.model.request.RegisterRequest
import com.slender.forumbackend.repository.RbacRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.repository.user.UserWriteRepository
import java.time.LocalDateTime
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class UserRegistrationService(
    private val redisTemplate: StringRedisTemplate,
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
    private val rbacRepository: RbacRepository,
    private val registerCaptchaValidator: RegisterCaptchaValidator,
    private val userFactory: UserFactory,
) {
    fun register(request: RegisterRequest) {
        registerCaptchaValidator.validate(request)
        val hasUser = userReadRepository.findByEmail(request.email) != null
        if (hasUser) throw UserAlreadyExistsException()
        val now = LocalDateTime.now()
        val uid = userWriteRepository.createUser(userFactory.create(request, now))
        userWriteRepository.createStatistics(uid)
        val defaultRoleId = rbacRepository.findEnabledRoleIdByCode(DEFAULT_ROLE_CODE)
            ?: error("Default role USER not found")
        rbacRepository.bindRole(uid, defaultRoleId, now)
        redisTemplate.delete(REGISTER_CAPTCHA + request.email)
    }

    private companion object {
        const val DEFAULT_ROLE_CODE = "USER"
    }
}
