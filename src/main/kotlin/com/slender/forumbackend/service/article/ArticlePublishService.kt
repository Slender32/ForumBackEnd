package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.article.ArticleFactory
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.exception.ArticleTagInvalidException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticlePublishData
import com.slender.forumbackend.model.request.ArticlePublishRequest
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.article.ArticleContentRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import com.slender.forumbackend.component.common.ContentModerationPolicy
import com.slender.forumbackend.component.common.ContentReviewWriter
import com.slender.forumbackend.component.common.ModerationStatus
import java.time.LocalDateTime.now
import org.springframework.stereotype.Service

@Service
class ArticlePublishService(
    private val articleContentRepository: ArticleContentRepository,
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val articleTagRepository: ArticleTagRepository,
    private val tagRepository: TagRepository,
    private val articleFactory: ArticleFactory,
    private val moderation: ContentModerationPolicy,
    private val reviews: ContentReviewWriter,
    private val users: com.slender.forumbackend.repository.user.UserWriteRepository,
) {
    fun publish(authorId: Long, request: ArticlePublishRequest): ArticlePublishData {
        if (request.tags.size > TAG_LIMIT)
            throw ArticleTagInvalidException("文章标签数量不能超过 $TAG_LIMIT")
        val createTime = now()
        val title = moderation.moderate(request.title)
        val summary = moderation.moderate(request.summary)
        val content = moderation.moderate(request.content)
        listOf(title, summary, content)
            .firstOrNull { it.status == ModerationStatus.BLOCKED }
            ?.let { throw InvalidRequestException("内容包含敏感词，无法提交") }
        val reviewRequired =
            listOf(title, summary, content).any { it.status == ModerationStatus.REVIEW_REQUIRED }
        val normalized =
            request.copy(title = title.text, summary = summary.text, content = content.text)
        val article = articleFactory.create(
            authorId, normalized, createTime,
            if (reviewRequired) ArticleStatus.Draft else ArticleStatus.Published,
        )
        val articleId = articleContentRepository.createArticle(article)

        articleContentRepository.createContent(articleId, content.text)
        articleStatisticRepository.createStatistic(articleId)

        normalized.tags
            .map { it.copy(name = it.name.trim()) }
            .distinctBy { it.name to it.color }
            .forEach { tagRequest ->
                val tag = tagRepository.findOrCreate(tagRequest.name, tagRequest.color, createTime)
                articleTagRepository.bindTag(articleId, tag.tid, createTime)
            }

        if (reviewRequired)
            reviews.enqueue(
                "ARTICLE",
                articleId,
                authorId,
                mapOf(
                    "articleId" to articleId,
                    "title" to article.title,
                    "summary" to article.summary,
                    "content" to normalized.content,
                    "cover" to article.cover,
                    "tags" to normalized.tags,
                    "operation" to "CREATE",
                ),
                content = normalized.content,
            )

        if (!reviewRequired) users.addPublishedArticleCount(authorId, 1)
        return ArticlePublishData(articleId = articleId, publishTime = createTime.timestamp)
    }

    private companion object {
        const val TAG_LIMIT = 10
    }
}
