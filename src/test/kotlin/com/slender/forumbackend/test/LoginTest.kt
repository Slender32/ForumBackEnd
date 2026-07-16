package com.slender.forumbackend.test

import com.jayway.jsonpath.JsonPath
import com.slender.forumbackend.TestcontainersConfiguration
import com.slender.forumbackend.constant.core.Redis.Key.USER_BLOCK
import com.slender.forumbackend.constant.core.Redis.Key.USER_LOGIN_CACHE
import com.slender.forumbackend.constant.core.Redis.Time.ACCESS_TOKEN_EXPIRE_TIME
import com.slender.forumbackend.constant.enumeration.user.Gender
import com.slender.forumbackend.constant.enumeration.user.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@TestConstructor(autowireMode = ALL)
@SpringBootTest
class LoginTest (
    private val mockMvc: MockMvc,
    private val jdbcTemplate: JdbcTemplate,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: StringRedisTemplate,
) {
    @BeforeEach
    fun cleanFakeData() {
        jdbcTemplate.update("delete from user_roles where user_id >= ? and user_id < ?", TEST_UID_BASE, TEST_UID_LIMIT)
        jdbcTemplate.update("delete from users where uid >= ? and uid < ?", TEST_UID_BASE, TEST_UID_LIMIT)
        TEST_UIDS.forEach { uid ->
            redisTemplate.delete(USER_LOGIN_CACHE + uid)
            redisTemplate.delete(USER_BLOCK + uid)
        }
    }

    @Test
    fun `login succeeds through auth success handler and writes cache`() {
        val user = fakeUser()

        login(user)

        val cacheKey = USER_LOGIN_CACHE + user.uid
        val cache = redisTemplate.opsForValue().get(cacheKey)
        val ttl = redisTemplate.getExpire(cacheKey, MILLISECONDS)
        assertNotNull(cache)
        assertTrue(cache.contains("ROLE_USER"))
        assertTrue(cache.contains("article:create"))
        assertTrue(cache.contains("comment:create"))
        assertTrue(ttl in 1..ACCESS_TOKEN_EXPIRE_TIME.toMillis())
    }

    @Test
    fun `login rejects duplicate active session`() {
        val user = fakeUser()
        login(user)

        mockMvc.perform(loginRequest(user.email, user.password))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `login rejects wrong password and unknown email`() {
        val user = fakeUser()

        mockMvc.perform(loginRequest(user.email, "wrong-password"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))

        mockMvc.perform(loginRequest("missing@example.com", user.password))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))
    }

    @Test
    fun `login rejects invalid request bodies`() {
        mockMvc.perform(loginRequest("", PASSWORD))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))

        mockMvc.perform(loginRequest("not-an-email", PASSWORD))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))

        mockMvc.perform(loginRequest(ACTIVE_EMAIL, ""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))

        mockMvc.perform(rawLoginRequest("""{"email":"$ACTIVE_EMAIL"}"""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))

        mockMvc.perform(
            post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content("{")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))
    }

    @Test
    fun `login rejects banned user and does not write cache`() {
        val user = fakeUser(uid = BANNED_UID, email = BANNED_EMAIL, status = UserStatus.BANNED)

        mockMvc.perform(loginRequest(user.email, user.password))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.code").value(403))

        assertNull(redisTemplate.opsForValue().get(USER_LOGIN_CACHE + user.uid))
    }

    @Test
    fun `refresh rejects when access session has not expired`() {
        val user = fakeUser()
        val tokens = login(user)

        mockMvc.perform(
            get("/auth/refresh")
                .header(AUTHORIZATION, bearer(tokens.refreshToken))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))
    }

    @Test
    fun `refresh succeeds after login cache expires and new access token can logout`() {
        val user = fakeUser()
        val tokens = login(user)
        redisTemplate.delete(USER_LOGIN_CACHE + user.uid)

        val refreshed = mockMvc.perform(
            get("/auth/refresh")
                .header(AUTHORIZATION, bearer(tokens.refreshToken))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.uid").value(user.uid))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty)
            .andReturn()

        val refreshedTokens = tokensFrom(refreshed.response.contentAsString)
        assertNotNull(redisTemplate.opsForValue().get(USER_LOGIN_CACHE + user.uid))

        mockMvc.perform(
            post("/users/${user.uid}/logout")
                .header(AUTHORIZATION, bearer(refreshedTokens.accessToken))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))
        assertNull(redisTemplate.opsForValue().get(USER_LOGIN_CACHE + user.uid))
    }

    @Test
    fun `refresh requires refresh bearer token`() {
        val user = fakeUser()
        val tokens = login(user)

        mockMvc.perform(get("/auth/refresh"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value(401))

        mockMvc.perform(
            get("/auth/refresh")
                .header(AUTHORIZATION, bearer(tokens.accessToken))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))
    }

    @Test
    fun `refresh rejects blocked user after access session expires`() {
        val user = fakeUser()
        val tokens = login(user)
        redisTemplate.delete(USER_LOGIN_CACHE + user.uid)
        redisTemplate.opsForValue().set(USER_BLOCK + user.uid, "blocked")

        mockMvc.perform(
            get("/auth/refresh")
                .header(AUTHORIZATION, bearer(tokens.refreshToken))
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.code").value(403))

        assertNull(redisTemplate.opsForValue().get(USER_LOGIN_CACHE + user.uid))
    }

    @Test
    fun `logout removes login cache and invalidates old access token`() {
        val user = fakeUser()
        val tokens = login(user)

        mockMvc.perform(
            post("/users/${user.uid}/logout")
                .header(AUTHORIZATION, bearer(tokens.accessToken))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))

        assertNull(redisTemplate.opsForValue().get(USER_LOGIN_CACHE + user.uid))

        mockMvc.perform(
            post("/users/${user.uid}/logout")
                .header(AUTHORIZATION, bearer(tokens.accessToken))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value(401))
    }

    @Test
    fun `logout requires valid access bearer token`() {
        val user = fakeUser()

        mockMvc.perform(post("/users/${user.uid}/logout"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value(401))

        mockMvc.perform(
            post("/users/${user.uid}/logout")
                .header(AUTHORIZATION, bearer("not-a-jwt"))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(400))
    }

    private fun login(user: TestUser): LoginTokens {
        val result = mockMvc.perform(loginRequest(user.email, user.password))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.uid").value(user.uid))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty)
            .andReturn()

        return tokensFrom(result.response.contentAsString)
    }

    private fun loginRequest(email: String, password: String) =
        rawLoginRequest("""{"email":"$email","password":"$password"}""")

    private fun rawLoginRequest(content: String) =
        post("/auth/login")
            .contentType(APPLICATION_JSON)
            .content(content)

    private fun fakeUser(
        uid: Long = ACTIVE_UID,
        name: String = "Login Test User",
        email: String = ACTIVE_EMAIL,
        password: String = PASSWORD,
        status: UserStatus = UserStatus.ACTIVE,
    ): TestUser {
        val now = LocalDateTime.now()
        jdbcTemplate.update(
            """
            insert into users(uid, name, email, password_hash, avatar, gender, signature, status, create_time, update_time)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            uid,
            name,
            email,
            passwordEncoder.encode(password),
            "https://example.com/avatar.png",
            Gender.Unknown.value,
            "fake login test user",
            status.value,
            now,
            now,
        )

        val roleId = jdbcTemplate.queryForObject<Long>(
            "select role_id from roles where code = ?",
            "USER",
        ) ?: error("Seed role USER not found")

        jdbcTemplate.update(
            "insert into user_roles(user_id, role_id, create_time) values (?, ?, ?)",
            uid,
            roleId,
            now,
        )
        return TestUser(uid, name, email, password)
    }

    private fun tokensFrom(responseBody: String): LoginTokens {
        val document = JsonPath.parse(responseBody)
        val accessToken: String = document.read("$.data.accessToken")
        val refreshToken: String = document.read("$.data.refreshToken")
        return LoginTokens(accessToken, refreshToken)
    }

    private fun bearer(token: String) = "Bearer $token"

    private data class TestUser(
        val uid: Long,
        val name: String,
        val email: String,
        val password: String,
    )

    private data class LoginTokens(
        val accessToken: String,
        val refreshToken: String,
    )

    private companion object {
        const val AUTHORIZATION = "Authorization"
        const val TEST_UID_BASE = 910_000L
        const val TEST_UID_LIMIT = 910_100L
        const val ACTIVE_UID = 910_001L
        const val BANNED_UID = 910_002L
        const val PASSWORD = "Passw0rd!"
        const val ACTIVE_EMAIL = "login-test@example.com"
        const val BANNED_EMAIL = "login-banned@example.com"

        val TEST_UIDS = (TEST_UID_BASE until TEST_UID_LIMIT).toList()
    }
}
