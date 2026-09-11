package com.slender.forumbackend.service.user

import com.slender.forumbackend.component.common.CaptchaGenerator
import com.slender.forumbackend.component.user.UserPasswordValidator
import com.slender.forumbackend.component.user.UserProfileValidator
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.enumeration.user.UserStatus.DELETED
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.exception.UserAlreadyExistsException
import com.slender.forumbackend.model.data.SessionData
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.model.data.user.CancelAccountData
import com.slender.forumbackend.model.data.user.UpdatePasswordData
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.request.CancelAccountRequest
import com.slender.forumbackend.model.request.RebindEmailRequest
import com.slender.forumbackend.model.request.UpdateAvatarRequest
import com.slender.forumbackend.model.request.UpdatePasswordRequest
import com.slender.forumbackend.model.request.UpdateSignatureRequest
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.repository.user.UserWriteRepository
import com.slender.forumbackend.component.common.ContentModerationPolicy
import com.slender.forumbackend.component.common.ContentReviewWriter
import com.slender.forumbackend.component.common.ModerationStatus
import com.slender.forumbackend.service.auth.UserTokenService
import java.time.Instant
import java.time.LocalDateTime.now
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAccountService(
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
    private val userProfileValidator: UserProfileValidator,
    private val userPasswordValidator: UserPasswordValidator,
    private val passwordEncoder: PasswordEncoder,
    private val captchaGenerator: CaptchaGenerator,
    private val userTokenService: UserTokenService,
    private val redisTemplate: StringRedisTemplate,
    private val moderation: ContentModerationPolicy,
    private val reviews: ContentReviewWriter,
) {
    @Transactional
    fun updateAvatar(uid: Long, request: UpdateAvatarRequest): UserData {
        userProfileValidator.validateAvatar(request.avatar)
        return updateUser(uid) { copy(avatar = request.avatar.trim()) }
    }

    @Transactional
    fun updateSignature(uid: Long, request: UpdateSignatureRequest): UserData {
        val result = moderation.moderate(request.signature.trim())
        if (result.status == ModerationStatus.BLOCKED) throw InvalidRequestException("内容包含敏感词，无法提交")
        userProfileValidator.validateSignature(result.text)
        if (result.status == ModerationStatus.REVIEW_REQUIRED) {
            val user = userReadRepository.findActiveByIdOrThrow(uid)
            reviews.enqueue(
                "USER_SIGNATURE",
                uid,
                uid,
                mapOf("uid" to uid, "signature" to result.text),
                result.text,
            )
            return user.toUserData(userReadRepository.findStatisticsById(uid))
        }
        return updateUser(uid) { copy(signature = result.text) }
    }

    @Transactional
    fun rebindEmail(uid: Long, request: RebindEmailRequest): SessionData {
        val user = userReadRepository.findActiveByIdOrThrow(uid)
        val newEmail = request.newEmail.trim()
        captchaGenerator.validate(newEmail, request.captcha)
        val existing = userReadRepository.findByEmail(newEmail)
        if (existing != null && existing.uid != uid) throw UserAlreadyExistsException()

        userWriteRepository.updateUser(user.copy(email = newEmail, updateTime = now()))
        captchaGenerator.consume(newEmail)
        return userTokenService.refresh(uid).toSessionData()
    }

    @Transactional
    fun updatePassword(uid: Long, request: UpdatePasswordRequest): UpdatePasswordData {
        val user = userReadRepository.findActiveByIdOrThrow(uid)

        userPasswordValidator.validateCurrent(request.oldPassword, user.passwordHash)
        userPasswordValidator.validateChanged(request.oldPassword, request.newPassword)

        userWriteRepository.updateUser(
            user.copy(
                passwordHash = passwordEncoder.encode(request.newPassword)!!,
                updateTime = now(),
            )
        )
        redisTemplate.delete(USER_LOGIN_CACHE + uid)
        return UpdatePasswordData(tokenInvalidated = true)
    }

    @Transactional
    fun cancelAccount(uid: Long, request: CancelAccountRequest): CancelAccountData {
        val user = userReadRepository.findActiveByIdOrThrow(uid)
        userPasswordValidator.validateCurrent(request.password, user.passwordHash)
        captchaGenerator.validate(user.email, request.captcha)

        val cancelledAt = Instant.now().toEpochMilli()
        userWriteRepository.updateUser(user.copy(status = DELETED, updateTime = now()))
        captchaGenerator.consume(user.email)
        redisTemplate.delete(USER_LOGIN_CACHE + uid)
        return CancelAccountData(uid, cancelledAt)
    }

    private fun updateUser(uid: Long, update: User.() -> User): UserData {
        val user = userReadRepository.findActiveByIdOrThrow(uid)
        val updated = update(user)
        userWriteRepository.updateUser(updated)
        return updated.toUserData(userReadRepository.findStatisticsById(uid))
    }
}
