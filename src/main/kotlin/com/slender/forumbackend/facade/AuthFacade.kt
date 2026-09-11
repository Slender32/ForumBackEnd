package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.CaptchaRequest
import com.slender.forumbackend.model.request.RegisterRequest
import com.slender.forumbackend.component.common.CaptchaGenerator
import com.slender.forumbackend.service.auth.UserRegistrationService
import com.slender.forumbackend.service.auth.UserTokenService
import com.slender.forumbackend.service.auth.CurrentUserQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthFacade(
    private val captchaGenerator: CaptchaGenerator,
    private val userRegistrationService: UserRegistrationService,
    private val userTokenService: UserTokenService,
    private val currentUserQueryService: CurrentUserQueryService,
) {
    fun sendCaptcha(captchaRequest: CaptchaRequest) = captchaGenerator.sendCaptcha(captchaRequest)
    @Transactional
    fun register(registerRequest: RegisterRequest) = userRegistrationService.register(registerRequest)
    fun refresh(uid: Long) = userTokenService.refresh(uid)
    fun currentUser(uid: Long) = currentUserQueryService.current(uid)
}
