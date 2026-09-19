package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.article.ArticleTagWriter
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Deleted
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.request.ArticleUpdateRequest
import com.slender.forumbackend.repository.FavoriteRepository
import com.slender.forumbackend.repository.article.ArticleContentRepository
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.repository.article.ArticlePromotionRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleRewardRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import com.slender.forumbackend.repository.comment.CommentContentRepository
import com.slender.forumbackend.repository.comment.CommentInteractionRepository
import com.slender.forumbackend.repository.comment.CommentNoticeRepository
import com.slender.forumbackend.component.common.ContentModerationPolicy
import com.slender.forumbackend.component.common.ContentReviewWriter
import com.slender.forumbackend.component.common.ModerationStatus
import com.slender.forumbackend.repository.user.UserWriteRepository
import java.time.LocalDateTime
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class ArticleCommandService(
    private val articles: ArticleQueryRepository,
    private val contents: ArticleContentRepository,
    private val interactions: ArticleInteractionRepository,
    private val promotions: ArticlePromotionRepository,
    private val rewards: ArticleRewardRepository,
    private val tags: ArticleTagRepository,
    private val favorites: FavoriteRepository,
    private val comments: CommentContentRepository,
    private val commentInteractions: CommentInteractionRepository,
    private val commentNotices: CommentNoticeRepository,
    private val moderation: ContentModerationPolicy,
    private val reviews: ContentReviewWriter,
    private val tagWriter: ArticleTagWriter,
    private val users: UserWriteRepository,
) {
    fun update(id: Long, userId: Long, authorities: Set<String>, request: ArticleUpdateRequest) {
        val current =
            articles.findByIdForUpdate(id)?.takeIf {
                it.deletedAt == null && it.status != Deleted
            } ?: throw IllegalArgumentException("文章不存在")
        if (current.authorId != userId && "article:manage" !in authorities)
            throw AccessDeniedException("NO_PERMISSION")
        val title = moderation.moderate(request.title.trim())
        val summary = moderation.moderate(request.summary.trim())
        val content = moderation.moderate(request.content.trim())
        listOf(title, summary, content)
            .firstOrNull { it.status == ModerationStatus.BLOCKED }
            ?.let { throw InvalidRequestException("内容包含敏感词，无法提交") }
        val now = LocalDateTime.now()
        if (listOf(title, summary, content).any { it.status == ModerationStatus.REVIEW_REQUIRED }) {
            reviews.enqueue(
                "ARTICLE",
                id,
                userId,
                mapOf(
                    "articleId" to id,
                    "title" to title.text,
                    "summary" to summary.text,
                    "content" to content.text,
                    "cover" to request.cover.trim(),
                    "tags" to request.tags,
                    "operation" to "UPDATE",
                ),
                content = content.text,
            )
            return
        }
        articles.updateById(
            current.copy(
                title = title.text,
                summary = summary.text,
                cover = request.cover.trim(),
                reviseTime = now,
                updateTime = now,
            )
        )
        contents.updateContent(id, content.text)
        request.tags?.let { tagWriter.replace(id, it, now) }
    }

    fun delete(id: Long, userId: Long, authorities: Set<String>) {
        val current = articles.findByIdForUpdate(id) ?: return
        if (current.deletedAt != null || current.status == Deleted) return
        if (current.authorId != userId && "article:manage" !in authorities)
            throw AccessDeniedException("NO_PERMISSION")
        val now = LocalDateTime.now()
        articles.updateById(
            current.copy(
                status = Deleted,
                updateTime = now,
                deletedAt = now,
            )
        )
        contents.markDeleted(id, now)
        promotions.markDeletedByArticle(id, now)
        tags.markDeletedByArticle(id, now)
        interactions.markDeletedByArticle(id, now)
        rewards.markDeletedByArticle(id, now)
        favorites.markDeletedByArticle(id, now)
        comments.markDeletedByArticle(id, now)
        commentInteractions.markDeletedByArticle(id, now)
        commentNotices.markDeletedByArticle(id, now)
        if (current.status == com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Published &&
            current.visibility == com.slender.forumbackend.constant.enumeration.article.ArticleVisibility.Public
        ) users.addPublishedArticleCount(current.authorId, -1)
    }
}
