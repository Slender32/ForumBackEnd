package com.slender.forumbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ForumBackEndApplication

fun main(args: Array<String>) {
    runApplication<ForumBackEndApplication>(*args)
}
