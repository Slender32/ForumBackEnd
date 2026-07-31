package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.core.Redis.Key.REGISTER_CAPTCHA
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.Redis.Time.ACCESS_TOKEN_EXPIRE_TIME
import com.slender.forumbackend.constant.enumeration.user.Gender.Unknown
import com.slender.forumbackend.constant.enumeration.user.UserStatus.ACTIVE
import com.slender.forumbackend.constant.util.JwtToolkit.accessToken
import com.slender.forumbackend.constant.util.JwtToolkit.refreshToken
import com.slender.forumbackend.exception.BlockException
import com.slender.forumbackend.exception.CaptchaInvalidException
import com.slender.forumbackend.exception.UserAlreadyExistsException
import com.slender.forumbackend.exception.UserNotFoundException
import com.slender.forumbackend.mapper.RbacRepository
import com.slender.forumbackend.mapper.UserMapper
import com.slender.forumbackend.mapper.UserRepository
import com.slender.forumbackend.model.cache.LoginDataCache
import com.slender.forumbackend.model.data.RefreshData
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.model.request.RegisterRequest
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface UserService : IService<User> {
    fun register(registerRequest: RegisterRequest)

    fun refresh(uid: Long): RefreshData

    fun currentUser(uid: Long): UserData
}

@Service
class UserServiceImpl(
    private val json: Json,
    private val redisTemplate: StringRedisTemplate,
    private val userRepository: UserRepository,
    private val rbacRepository: RbacRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserService, ServiceImpl<UserMapper, User>() {

    @Transactional
    override fun register(registerRequest: RegisterRequest) {
        val email = registerRequest.email
        val captchaKey = REGISTER_CAPTCHA + email
        val cachedCaptcha = redisTemplate.opsForValue().get(captchaKey)
        if (cachedCaptcha != registerRequest.captcha) throw CaptchaInvalidException()

        if (userRepository.findByEmail(email) != null) throw UserAlreadyExistsException()

        val now = LocalDateTime.now()
        val uid = userRepository.createUser(
            User(
                name = registerRequest.name.trim(),
                email = email,
                passwordHash = passwordEncoder.encode(registerRequest.password)!!,
                avatar = DEFAULT_AVATAR,
                gender = Unknown,
                signature = DEFAULT_SIGNATURE,
                status = ACTIVE,
                createTime = now,
                updateTime = now,
            )
        )

        userRepository.createStatistics(uid)
        val defaultRoleId = rbacRepository.findEnabledRoleIdByCode(DEFAULT_ROLE_CODE)
            ?: error("Default role USER not found")
        rbacRepository.bindRole(uid, defaultRoleId, now)
        redisTemplate.delete(captchaKey)
    }

    override fun refresh(uid: Long): RefreshData {
        val user = checkAndGetUser(uid)
        val authorities = rbacRepository.findAuthoritiesByUserId(uid)
        val loginDataCache = user.run { LoginDataCache(uid, name, email, avatar, authorities) }
        redisTemplate.opsForValue().set(
            USER_LOGIN_CACHE + uid,
            json.format(loginDataCache),
            ACCESS_TOKEN_EXPIRE_TIME,
        )
        val statistics = userRepository.findStatisticsById(uid)
        return RefreshData(
            accessToken(uid),
            refreshToken(uid),
            userData = user.toUserData(statistics),
        )
    }

    override fun currentUser(uid: Long): UserData {
        val statistics = userRepository.findStatisticsById(uid)
        return checkAndGetUser(uid).toUserData(statistics)
    }

    private final fun checkAndGetUser(uid: Long): User {
        val user = getById(uid) ?: throw UserNotFoundException()
        return user.also { if (it.status != ACTIVE) throw BlockException() }
    }

    private companion object {
        const val DEFAULT_ROLE_CODE = "USER"
        const val DEFAULT_AVATAR = "https://example.com/default-avatar.png"
        const val DEFAULT_SIGNATURE = ""
    }
}
