package com.slender.forumbackend.component.comment

import com.slender.forumbackend.exception.CommentContentInvalidException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.component.common.ContentModerationPolicy
import com.slender.forumbackend.component.common.ModerationResult
import com.slender.forumbackend.component.common.ModerationStatus
import org.springframework.stereotype.Component

@Component
class CommentContentValidator(
    private val sensitive: ContentModerationPolicy
) {
    fun validate(content: String): String {
        val trimmed = content.trim()
        if (trimmed.isEmpty() || trimmed.length > CONTENT_LIMIT)
            throw CommentContentInvalidException()
        val result = sensitive.moderate(trimmed)
        val invalid = result.status.let { it == ModerationStatus.BLOCKED || it == ModerationStatus.REVIEW_REQUIRED }
        if (invalid) throw InvalidRequestException("内容包含敏感词，无法提交")
        return result.text
    }

    fun validateResult(content: String): ModerationResult {
        val trimmed = content.trim()
        if (trimmed.isEmpty() || trimmed.length > CONTENT_LIMIT)
            throw CommentContentInvalidException()
        val moderated = sensitive.moderate(trimmed)
        if (moderated.status == ModerationStatus.BLOCKED)
            throw InvalidRequestException("内容包含敏感词，无法提交")
        return moderated
    }

    private companion object {
        const val CONTENT_LIMIT = 2000
    }
}
