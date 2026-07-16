package com.slender.forumbackend

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<ForumBackEndApplication>()
        .with(TestcontainersConfiguration::class).run(*args)
}
