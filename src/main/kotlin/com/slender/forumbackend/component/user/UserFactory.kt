package com.slender.forumbackend.component.user

import com.slender.forumbackend.constant.enumeration.user.Gender.Unknown
import com.slender.forumbackend.constant.enumeration.user.UserStatus.ACTIVE
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.request.RegisterRequest
import java.time.LocalDateTime
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserFactory(
    private val passwordEncoder: PasswordEncoder,
) {
    fun create(request: RegisterRequest, now: LocalDateTime) =
        User(
            name = request.name.trim(),
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)!!,
            avatar = DEFAULT_AVATAR,
            gender = Unknown,
            signature = DEFAULT_SIGNATURE,
            status = ACTIVE,
            createTime = now,
            updateTime = now,
        )

    private companion object {
        const val DEFAULT_AVATAR = "https://example.com/default-avatar.png"
        const val DEFAULT_SIGNATURE = ""
    }
}
