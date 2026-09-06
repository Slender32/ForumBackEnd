package com.slender.forumbackend.component.user

import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.exception.LoginMisMatchException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserPasswordValidator(
    private val passwordEncoder: PasswordEncoder,
) {
    fun validateCurrent(rawPassword: String, passwordHash: String) {
        if (!passwordEncoder.matches(rawPassword, passwordHash))
            throw LoginMisMatchException("邮箱或密码错误")
    }

    fun validateChanged(oldPassword: String, newPassword: String) {
        if (oldPassword == newPassword)
            throw InvalidRequestException("新密码不能与旧密码相同")
    }
}
