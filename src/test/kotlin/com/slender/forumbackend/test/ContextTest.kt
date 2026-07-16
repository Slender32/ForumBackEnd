package com.slender.forumbackend.test

import com.slender.forumbackend.TestcontainersConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import kotlin.test.Test

@TestConstructor(autowireMode = ALL)
@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ContextTest(

) {

    @Test
    fun contextLoads() {

    }
}
