package com.slender.forumbackend.component.article

import kotlin.text.RegexOption.MULTILINE
import org.springframework.stereotype.Component

@Component
class MarkdownSummaryGenerator {
    fun generate(content: String): String =
        content
            .replace(Regex("(?s)```.*?```"), " ")
            .replace(Regex("`([^`]*)`"), "$1")
            .replace(Regex("!\\[[^]]*]\\([^)]*\\)"), " ")
            .replace(Regex("\\[([^]]+)]\\([^)]*\\)"), "$1")
            .replace(Regex("^\\s{0,3}#{1,6}\\s*", MULTILINE), "")
            .replace(Regex("^\\s{0,3}>\\s?", MULTILINE), "")
            .replace(Regex("^\\s*[-*+]\\s+", MULTILINE), "")
            .replace(Regex("^\\s*\\d+\\.\\s+", MULTILINE), "")
            .replace(Regex("[*_~#>|\\-]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(SUMMARY_LIMIT)

    private companion object {
        const val SUMMARY_LIMIT = 160
    }
}
