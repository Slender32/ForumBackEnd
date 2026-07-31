package com.slender.forumbackend.test

import com.slender.forumbackend.TestcontainersConfiguration
import com.slender.forumbackend.constant.enumeration.error.Error.CAPTCHA_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.REQUEST_CONTENT_INVALID
import com.slender.forumbackend.constant.enumeration.error.Error.USER_ALREADY_EXISTS
import com.slender.forumbackend.constant.core.Redis.Key.REGISTER_CAPTCHA
import com.slender.forumbackend.constant.core.Redis.Time.REGISTER_CAPTCHA_EXPIRE_TIME
import com.slender.forumbackend.constant.enumeration.user.Gender
import com.slender.forumbackend.constant.enumeration.user.UserStatus
import jakarta.mail.BodyPart
import jakarta.mail.Multipart
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.InputStream
import java.time.LocalDateTime
import java.util.Properties
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestConfiguration(proxyBeanMethods = false)
class RegisterTestConfiguration {
    @Bean
    @Primary
    fun javaMailSender(): JavaMailSender = mock(JavaMailSender::class.java)
}

@Import(
    TestcontainersConfiguration::class,
    RegisterTestConfiguration::class,
)
@AutoConfigureMockMvc
@TestConstructor(autowireMode = ALL)
@SpringBootTest(
    properties = [
        "spring.mail.username=test-sender@example.com"
    ]
)
class RegisterTest(
    private val mockMvc: MockMvc,
    private val jdbcTemplate: JdbcTemplate,
    private val redisTemplate: StringRedisTemplate,
    private val passwordEncoder: PasswordEncoder,
    private val mailSender: JavaMailSender,
) {
    @BeforeEach
    fun cleanData() {
        reset(mailSender)
        `when`(mailSender.createMimeMessage())
            .thenReturn(MimeMessage(Session.getInstance(Properties())))
        TEST_EMAILS.forEach { email ->
            redisTemplate.delete(REGISTER_CAPTCHA + email)
        }
        jdbcTemplate.update(
            "delete from user_roles where user_id in (select uid from users where email in (?, ?, ?, ?, ?, ?))",
            CAPTCHA_EMAIL,
            REGISTER_EMAIL,
            LEGACY_FIELD_EMAIL,
            DUPLICATE_EMAIL,
            FIRST_SEQUENTIAL_EMAIL,
            SECOND_SEQUENTIAL_EMAIL,
        )
        jdbcTemplate.update(
            "delete from user_stats where user_id in (select uid from users where email in (?, ?, ?, ?, ?, ?))",
            CAPTCHA_EMAIL,
            REGISTER_EMAIL,
            LEGACY_FIELD_EMAIL,
            DUPLICATE_EMAIL,
            FIRST_SEQUENTIAL_EMAIL,
            SECOND_SEQUENTIAL_EMAIL,
        )
        jdbcTemplate.update(
            "delete from users where email in (?, ?, ?, ?, ?, ?)",
            CAPTCHA_EMAIL,
            REGISTER_EMAIL,
            LEGACY_FIELD_EMAIL,
            DUPLICATE_EMAIL,
            FIRST_SEQUENTIAL_EMAIL,
            SECOND_SEQUENTIAL_EMAIL,
        )
    }

    @Test
    fun `captcha endpoint writes redis and sends html mail asynchronously`() {
        mockMvc.perform(captchaRequest(CAPTCHA_EMAIL))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))

        val captcha = redisTemplate.opsForValue().get(REGISTER_CAPTCHA + CAPTCHA_EMAIL)
        assertNotNull(captcha)
        assertTrue(captcha.matches(Regex("^[1-9]\\d{5}$")))

        val captor = ArgumentCaptor.forClass(MimeMessage::class.java)
        verify(mailSender, timeout(2000)).send(captor.capture())
        val message = captor.value

        assertEquals(MAIL_SUBJECT, message.subject)
        assertTrue(message.from.first().toString().contains(FROM_PERSONAL))
        assertTrue(message.from.first().toString().contains(FROM_EMAIL))
        assertTrue(message.allRecipients.map { it.toString() }.contains(CAPTCHA_EMAIL))
        println(message.contentType)

        val content = extractMailContent(message)
        assertTrue(content.contains("<html", ignoreCase = true))
        assertTrue(content.contains("Forum"))
        assertTrue(content.contains(captcha))
        assertTrue(content.contains(MAIL_TITLE))
        assertTrue(content.contains(MAIL_MESSAGE))
    }

    @Test
    fun `register creates user stats role and clears captcha`() {
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + REGISTER_EMAIL,
            SUCCESS_CAPTCHA,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )

        mockMvc.perform(registerRequest("""{"name":"register-user","password":"$PASSWORD","email":"$REGISTER_EMAIL","captcha":"$SUCCESS_CAPTCHA"}"""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))

        val uid = jdbcTemplate.queryForObject<Long>(
            "select uid from users where email = ?",
            REGISTER_EMAIL,
        )
        assertNotNull(uid)

        val passwordHash = jdbcTemplate.queryForObject<String>(
            "select password_hash from users where uid = ?",
            uid,
        )
        assertNotNull(passwordHash)
        assertTrue(passwordEncoder.matches(PASSWORD, passwordHash))

        val statusValue = jdbcTemplate.queryForObject<String>(
            "select status from users where uid = ?",
            uid,
        )
        val genderValue = jdbcTemplate.queryForObject<String>(
            "select gender from users where uid = ?",
            uid,
        )
        assertEquals(UserStatus.ACTIVE.value, statusValue)
        assertEquals(Gender.Unknown.value, genderValue)

        val statsCount = jdbcTemplate.queryForObject<Int>(
            "select count(*) from user_stats where user_id = ?",
            uid,
        )
        val roleCode = jdbcTemplate.queryForObject<String>(
            """
            select r.code
            from user_roles ur
            join roles r on r.role_id = ur.role_id
            where ur.user_id = ?
            """.trimIndent(),
            uid,
        )
        assertEquals(1, statsCount)
        assertEquals("USER", roleCode)
        assertNull(redisTemplate.opsForValue().get(REGISTER_CAPTCHA + REGISTER_EMAIL))
    }

    @Test
    fun `register creates distinct ids for two different emails sequentially`() {
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + FIRST_SEQUENTIAL_EMAIL,
            FIRST_SEQUENTIAL_CAPTCHA,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + SECOND_SEQUENTIAL_EMAIL,
            SECOND_SEQUENTIAL_CAPTCHA,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )

        mockMvc.perform(registerRequest("""{"name":"first-sequential","password":"$PASSWORD","email":"$FIRST_SEQUENTIAL_EMAIL","captcha":"$FIRST_SEQUENTIAL_CAPTCHA"}"""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))

        mockMvc.perform(registerRequest("""{"name":"second-sequential","password":"$PASSWORD","email":"$SECOND_SEQUENTIAL_EMAIL","captcha":"$SECOND_SEQUENTIAL_CAPTCHA"}"""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))

        val firstUid = assertNotNull(
            jdbcTemplate.queryForObject<Long>(
                "select uid from users where email = ?",
                FIRST_SEQUENTIAL_EMAIL,
            )
        )
        val secondUid = assertNotNull(
            jdbcTemplate.queryForObject<Long>(
                "select uid from users where email = ?",
                SECOND_SEQUENTIAL_EMAIL,
            )
        )

        assertTrue(firstUid > 0)
        assertTrue(secondUid > 0)
        assertNotEquals(firstUid, secondUid)
        assertEquals(1, countUserStats(firstUid))
        assertEquals(1, countUserStats(secondUid))
        assertEquals("USER", findRoleCode(firstUid))
        assertEquals("USER", findRoleCode(secondUid))
        assertNull(redisTemplate.opsForValue().get(REGISTER_CAPTCHA + FIRST_SEQUENTIAL_EMAIL))
        assertNull(redisTemplate.opsForValue().get(REGISTER_CAPTCHA + SECOND_SEQUENTIAL_EMAIL))
    }

    @Test
    fun `register accepts legacy captcha field alias`() {
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + LEGACY_FIELD_EMAIL,
            SUCCESS_CAPTCHA,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )

        mockMvc.perform(
            registerRequest(
                """{"name":"legacy-alias","password":"$PASSWORD","email":"$LEGACY_FIELD_EMAIL","captcha":"$SUCCESS_CAPTCHA"}"""
            )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))

        val uid = jdbcTemplate.queryForObject<Long>(
            "select uid from users where email = ?",
            LEGACY_FIELD_EMAIL,
        )
        assertNotNull(uid)
    }

    @Test
    fun `register rejects wrong captcha and malformed captcha format`() {
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + REGISTER_EMAIL,
            SUCCESS_CAPTCHA,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )

        mockMvc.perform(registerRequest("""{"name":"wrong-captcha","password":"$PASSWORD","email":"$REGISTER_EMAIL","captcha":"654321"}"""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(CAPTCHA_INVALID.code))

        mockMvc.perform(registerRequest("""{"name":"bad-format","password":"$PASSWORD","email":"$REGISTER_EMAIL","captcha":"012345"}"""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))
    }

    @Test
    fun `register rejects duplicate email`() {
        fakeExistingUser(DUPLICATE_EMAIL)
        redisTemplate.opsForValue().set(
            REGISTER_CAPTCHA + DUPLICATE_EMAIL,
            SUCCESS_CAPTCHA,
            REGISTER_CAPTCHA_EXPIRE_TIME,
        )

        mockMvc.perform(registerRequest("""{"name":"duplicate-user","password":"$PASSWORD","email":"$DUPLICATE_EMAIL","captcha":"$SUCCESS_CAPTCHA"}"""))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.code").value(USER_ALREADY_EXISTS.code))
    }

    private fun captchaRequest(email: String) =
        post("/auth/captcha")
            .contentType(APPLICATION_JSON)
            .content("""{"email":"$email"}""")

    private fun registerRequest(content: String) =
        post("/auth/register")
            .contentType(APPLICATION_JSON)
            .content(content)

    private fun fakeExistingUser(email: String) {
        val now = LocalDateTime.now()
        jdbcTemplate.update(
            """
            insert into users(name, email, password_hash, avatar, gender, signature, status, create_time, update_time)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            "duplicate-user",
            email,
            passwordEncoder.encode(PASSWORD),
            "https://example.com/avatar.png",
            Gender.Unknown.value,
            "",
            UserStatus.ACTIVE.value,
            now,
            now,
        )
    }

    private fun countUserStats(uid: Long): Int =
        jdbcTemplate.queryForObject<Int>(
            "select count(*) from user_stats where user_id = ?",
            uid,
        ) ?: 0

    private fun findRoleCode(uid: Long): String? =
        jdbcTemplate.queryForObject<String>(
            """
            select r.code
            from user_roles ur
            join roles r on r.role_id = ur.role_id
            where ur.user_id = ?
            """.trimIndent(),
            uid,
        )

    private fun extractMailContent(message: MimeMessage): String =
        extractContent(message.content)

    private fun extractContent(content: Any?): String = when (content) {
        null -> ""
        is String -> content
        is Multipart -> buildString {
            for (index in 0 until content.count) {
                append(extractContent(content.getBodyPart(index)))
            }
        }
        is BodyPart -> extractContent(content.content)
        is InputStream -> content.bufferedReader(Charsets.UTF_8).use { it.readText() }
        else -> content.toString()
    }
    
    private companion object {
        const val FROM_PERSONAL = "Himukai Kanata"
        const val FROM_EMAIL = "test-sender@example.com"
        const val MAIL_SUBJECT = "注册验证码"
        const val MAIL_TITLE = "Forum 注册验证码"
        const val MAIL_MESSAGE = "您正在进行注册操作，请使用以下验证码完成验证："
        const val PASSWORD = "Passw0rd!"
        const val SUCCESS_CAPTCHA = "123456"
        const val CAPTCHA_EMAIL = "captcha-register@example.com"
        const val REGISTER_EMAIL = "register-success@example.com"
        const val LEGACY_FIELD_EMAIL = "register-legacy@example.com"
        const val DUPLICATE_EMAIL = "register-duplicate@example.com"
        const val FIRST_SEQUENTIAL_EMAIL = "register-sequential-first@example.com"
        const val SECOND_SEQUENTIAL_EMAIL = "register-sequential-second@example.com"
        const val FIRST_SEQUENTIAL_CAPTCHA = "234567"
        const val SECOND_SEQUENTIAL_CAPTCHA = "345678"

        val TEST_EMAILS = listOf(
            CAPTCHA_EMAIL,
            REGISTER_EMAIL,
            LEGACY_FIELD_EMAIL,
            DUPLICATE_EMAIL,
            FIRST_SEQUENTIAL_EMAIL,
            SECOND_SEQUENTIAL_EMAIL,
        )
    }
}
