package com.slender.forumbackend.service.auth

import com.slender.forumbackend.component.common.CaptchaGenerator
import com.slender.forumbackend.exception.UserNotFoundException
import com.slender.forumbackend.model.request.ForgotPasswordRequest
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.repository.user.UserWriteRepository
import java.time.LocalDateTime
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PasswordResetService(
    private val captchaGenerator: CaptchaGenerator,
    private val passwordEncoder: PasswordEncoder,
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
) {
    @Transactional
    fun reset(request: ForgotPasswordRequest) {
        val email = request.email.trim()
        val user = userReadRepository.findByEmail(email) ?: throw UserNotFoundException()

        captchaGenerator.validate(user.email, request.captcha)
        userWriteRepository.updateUser(
            user.copy(
                passwordHash = passwordEncoder.encode(request.newPassword)!!,
                updateTime = LocalDateTime.now(),
            )
        )
        captchaGenerator.consume(user.email)
    }
}
