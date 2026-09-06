package com.slender.forumbackend.component.comment

import com.slender.forumbackend.exception.CommentContentInvalidException
import org.springframework.stereotype.Component

@Component
class CommentContentValidator {
    fun validate(content: String): String {
        val trimmed = content.trim()
        if (trimmed.isEmpty() || trimmed.length > CONTENT_LIMIT)
            throw CommentContentInvalidException()
        return trimmed
    }

    private companion object {
        const val CONTENT_LIMIT = 2000
    }
}
