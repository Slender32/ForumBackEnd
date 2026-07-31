package com.slender.forumbackend.service

import com.slender.forumbackend.constant.core.Redis.Key.REGISTER_CAPTCHA
import com.slender.forumbackend.constant.core.Redis.Time.REGISTER_CAPTCHA_EXPIRE_TIME
import com.slender.forumbackend.model.request.CaptchaRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom.current

interface CaptchaService {
    fun sendCaptcha(captchaRequest: CaptchaRequest)
}

@Service
class CaptchaServiceImpl(
    private val redisTemplate: StringRedisTemplate,
    private val emailService: EmailService,
) : CaptchaService {
    override fun sendCaptcha(captchaRequest: CaptchaRequest) {
        val captcha = current().nextInt(100000, 1_000_000).toString()
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + captchaRequest.email,
            captcha,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )
        emailService.sendRegisterCaptcha(captchaRequest.email, captcha)
    }
}
