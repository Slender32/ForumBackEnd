package com.slender.forumbackend.component.common

import com.slender.forumbackend.service.SensitiveWordQueryService
import org.springframework.stereotype.Component

enum class ModerationStatus {
    ALLOW,
    REPLACED,
    REVIEW_REQUIRED,
    BLOCKED,
}

data class ModerationResult(val status: ModerationStatus, val text: String)

@Component
class ContentModerationPolicy(private val words: SensitiveWordQueryService) {
    fun moderate(input: String): ModerationResult = words.evaluate(input)
}