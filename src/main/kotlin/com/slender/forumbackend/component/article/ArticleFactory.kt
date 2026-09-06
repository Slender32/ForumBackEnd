package com.slender.forumbackend.component.article

import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Published
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility.Public
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.request.ArticlePublishRequest
import java.time.LocalDateTime
import org.springframework.stereotype.Component

@Component
class ArticleFactory(
    private val markdownSummaryGenerator: MarkdownSummaryGenerator,
) {
    fun create(authorId: Long, request: ArticlePublishRequest, now: LocalDateTime): Article =
        Article(
            authorId = authorId,
            title = request.title.trim(),
            summary = request.summary.trim().takeUnless { it.isBlank() }
                ?: markdownSummaryGenerator.generate(request.content),
            cover = request.cover.trim(),
            status = Published,
            visibility = Public,
            publishTime = now,
            reviseTime = now,
            createTime = now,
            updateTime = now,
        )
}
