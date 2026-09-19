package com.slender.forumbackend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

val BUSINESS_ZONE: ZoneId = ZoneId.of("Asia/Shanghai")

@Configuration
class BusinessTime {
    @Bean
    fun businessClock(): Clock = Clock.system(BUSINESS_ZONE)
}
