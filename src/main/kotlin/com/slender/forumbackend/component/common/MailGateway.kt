package com.slender.forumbackend.component.common

import com.slender.forumbackend.library.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class MailGateway(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,

    @Value($$"${spring.mail.username}")
    private val fromAddress: String,
) {
    @Async
    fun sendRegisterCaptcha(email: String, captcha: String) {
        runCatching {
            val context = Context().apply {
                setVariables(
                    mapOf(
                        CODE_KEY to captcha,
                        TITLE_KEY to "Forum 注册验证码",
                        MESSAGE_KEY to "您正在进行注册操作，请使用以下验证码完成验证：",
                    )
                )
            }
            val htmlContent = templateEngine.process(TEMPLATE, context)
            val message = mailSender.createMimeMessage()
            MimeMessageHelper(message, true).run {
                setFrom(fromAddress, PERSONAL)
                setTo(email)
                setSubject("注册验证码")
                setText(htmlContent, true)
            }
            mailSender.send(message)
        }.onFailure {
            log.error("Failed to send register captcha to {}", email, it)
        }
    }

    private companion object {
        const val TEMPLATE = "VerificationCode"
        const val CODE_KEY = "code"
        const val TITLE_KEY = "messageTitle"
        const val MESSAGE_KEY = "message"
        const val PERSONAL = "Himukai Kanata"
        val log = logger()
    }
}
