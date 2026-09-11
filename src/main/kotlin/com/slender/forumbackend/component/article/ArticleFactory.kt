package com.slender.forumbackend.component.article

import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Published
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility.Public
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.request.ArticlePublishRequest
import java.time.LocalDateTime
import org.springframework.stereotype.Component
import kotlin.text.RegexOption.MULTILINE

@Component
class ArticleFactory {
    fun create(
        authorId: Long,
        request: ArticlePublishRequest,
        now: LocalDateTime,
        status: ArticleStatus = Published,
    ): Article =
        Article(
            authorId = authorId,
            title = request.title.trim(),
            summary =
                request.summary.trim().takeUnless { it.isBlank() }
                    ?: request.content.generate(),
            cover = request.cover.trim(),
            status = status,
            visibility = Public,
            publishTime = now,
            reviseTime = now,
            createTime = now,
            updateTime = now,
        )



    private companion object {
        const val SUMMARY_LIMIT = 160

        fun String.generate(): String =
            this.replace(Regex("(?s)```.*?```"), " ")
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
    }
}
