package com.slender.forumbackend.configuration.seed

import com.slender.forumbackend.library.logger
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("dev")
@Component
class DemoDataSeeder(
    private val userDataSeeder: UserDataSeeder,
    private val articleDataSeeder: ArticleDataSeeder,
    private val messageDataSeeder: MessageDataSeeder,
    @Value($$"${forum.seed.demo.enabled:false}") private val enabled: Boolean,
) : ApplicationRunner {
    private val log = logger()

    @Transactional
    override fun run(args: ApplicationArguments) {
        log.info("DemoDataSeeder loaded: enabled={}", enabled)
        if (!enabled) {
            log.info("DemoDataSeeder skipped because forum.seed.demo.enabled=false")
            return
        }

        log.info("DemoDataSeeder seeding demo data")
        val now = FIXED_NOW
        val users = userDataSeeder.seed(now)
        val articles = articleDataSeeder.seed(now, users)
        messageDataSeeder.seed(now, users)
        log.info("DemoDataSeeder completed: users={}, articles={}", users, articles)
    }

    private companion object {
        val FIXED_NOW: LocalDateTime = LocalDateTime.of(2026, 8, 19, 18, 30)
    }
}
