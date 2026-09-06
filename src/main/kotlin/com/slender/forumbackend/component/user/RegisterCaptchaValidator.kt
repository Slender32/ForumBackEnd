package com.slender.forumbackend.component.user

import com.slender.forumbackend.constant.core.Redis.Key.REGISTER_CAPTCHA
import com.slender.forumbackend.exception.CaptchaInvalidException
import com.slender.forumbackend.model.request.RegisterRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RegisterCaptchaValidator(
    private val redisTemplate: StringRedisTemplate,
) {
    fun validate(request: RegisterRequest) {
        val key = REGISTER_CAPTCHA + request.email
        val captcha = redisTemplate.opsForValue().get(key)
        if (captcha != request.captcha) throw CaptchaInvalidException()
    }
}
