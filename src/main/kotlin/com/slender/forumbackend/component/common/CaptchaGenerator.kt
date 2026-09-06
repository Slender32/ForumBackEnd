package com.slender.forumbackend.component.common

import com.slender.forumbackend.constant.core.Redis.Key.REGISTER_CAPTCHA
import com.slender.forumbackend.constant.core.Redis.Time.REGISTER_CAPTCHA_EXPIRE_TIME
import com.slender.forumbackend.exception.CaptchaInvalidException
import com.slender.forumbackend.model.request.CaptchaRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom.current

@Component
class CaptchaGenerator(
    private val redisTemplate: StringRedisTemplate,
    private val mailGateway: MailGateway,
) {
    fun sendCaptcha(captchaRequest: CaptchaRequest) {
        val captcha = current().nextInt(100000, 1_000_000).toString()
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + captchaRequest.email,
            captcha,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )
        mailGateway.sendRegisterCaptcha(captchaRequest.email, captcha)
    }

    fun validate(email: String, captcha: String) {
        val value = redisTemplate.opsForValue().get(REGISTER_CAPTCHA + email)
        if (value != captcha) throw CaptchaInvalidException()
    }

    fun consume(email: String) {
        redisTemplate.delete(REGISTER_CAPTCHA + email)
    }
}
