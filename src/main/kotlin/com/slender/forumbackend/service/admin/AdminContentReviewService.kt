package com.slender.forumbackend.service.admin

import com.slender.forumbackend.component.common.AuditLogger
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import com.slender.forumbackend.constant.enumeration.notice.CommentNoticeType
import com.slender.forumbackend.exception.AdminResourceNotFoundException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.governance.ContentReviewData
import com.slender.forumbackend.model.entity.governance.ContentReviewRecord
import com.slender.forumbackend.model.request.ContentReviewDecisionRequest
import com.slender.forumbackend.repository.article.ArticleContentRepository
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.comment.CommentContentRepository
import com.slender.forumbackend.repository.comment.CommentStatisticRepository
import com.slender.forumbackend.repository.governance.ContentReviewRepository
import com.slender.forumbackend.repository.user.UserWriteRepository
import com.slender.forumbackend.service.comment.CommentNoticeService
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime

@Service
class AdminContentReviewService(
    private val reviews: ContentReviewRepository,
    private val articles: ArticleQueryRepository,
    private val articleContents: ArticleContentRepository,
    private val articleStats: ArticleStatisticRepository,
    private val comments: CommentContentRepository,
    private val commentStats: CommentStatisticRepository,
    private val commentNotices: CommentNoticeService,
    private val users: UserWriteRepository,
    private val audit: AuditLogger,
    private val mapper: ObjectMapper,
) {
    fun list(status: String? = null, page: Int = 1, size: Int = 20): AdminPageData<ContentReviewData> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        val normalized = status?.trim()?.takeIf { it.isNotEmpty() }?.uppercase()
        return AdminPageData(
            reviews.list(normalized, safePage, safeSize).map(ContentReviewRecord::toData),
            safePage,
            safeSize,
            reviews.count(normalized),
        )
    }

    fun get(id: Long): ContentReviewData = reviews.find(id)
        ?.toData()
        ?: throw AdminResourceNotFoundException("审核任务不存在")

    fun decide(id: Long, operator: Long, req: ContentReviewDecisionRequest): Int {
        val row = reviews.findPendingForUpdate(id)
            ?: throw AdminResourceNotFoundException("审核任务不存在")
        if (row.status != "PENDING") throw AdminResourceNotFoundException("审核任务已处理")
        if (req.approve) applyPayload(row) else rejectResource(row)
        val updated = reviews.updateDecision(id, operator, if (req.approve) "APPROVED" else "REJECTED", req.note)
        if (updated <= 0) throw AdminResourceNotFoundException("审核任务已处理")
        audit.log(
            operator,
            "content-review:manage",
            "content_review",
            id.toString(),
            if (req.approve) "APPROVE" else "REJECT",
            req.note,
            "SUCCESS",
        )
        return updated
    }

    private fun applyPayload(row: ContentReviewRecord) {
        val type = row.resourceType
        val id = row.resourceId?.toLongOrNull()
        @Suppress("UNCHECKED_CAST")
        val payload = row.payloadJson?.let { mapper.readValue(it, Map::class.java) as Map<String, Any?> }
            ?: mapOf("content" to row.content)
        val now = LocalDateTime.now()
        when (type) {
            "ARTICLE" -> {
                val articleId = id ?: payload.long("articleId")
                    ?: throw InvalidRequestException("文章 ID 缺失")
                if (!articles.approve(articleId, payload.string("title"), payload.string("summary"), payload.string("cover"), now)) {
                    throw AdminResourceNotFoundException("待审核文章不存在或已删除")
                }
                payload.string("content")?.let { articleContents.updateContent(articleId, it) }
            }
            "COMMENT" -> applyComment(id, payload, now)
            "USER_SIGNATURE" -> {
                val uid = payload.long("uid") ?: id ?: throw InvalidRequestException("用户 ID 缺失")
                if (!users.updateSignature(uid, payload.string("signature") ?: "", now)) {
                    throw AdminResourceNotFoundException("待审核用户不存在或不可用")
                }
            }
            else -> throw InvalidRequestException("不支持的审核资源类型: $type")
        }
    }

    private fun applyComment(resourceId: Long?, payload: Map<String, Any?>, now: LocalDateTime) {
        val commentId = resourceId ?: payload.long("commentId")
            ?: throw InvalidRequestException("评论 ID 缺失")
        val current = comments.find(commentId)
            ?: throw AdminResourceNotFoundException("待审核评论不存在或已删除")
        if (current.deletedAt != null) throw AdminResourceNotFoundException("待审核评论不存在或已删除")
        val wasPending = current.status == CommentStatus.PendingReview
        if (!comments.approve(commentId, payload.string("content") ?: current.content, now)) {
            throw AdminResourceNotFoundException("待审核评论不存在或已删除")
        }
        if (!wasPending) return
        commentStats.createStatisticsIfAbsent(commentId)
        articleStats.incrementCommentCount(current.articleId, 1)
        if (current.rootCommentId > 0) commentStats.incrementReplyCount(current.rootCommentId, 1)
        val recipient =
            if (current.rootCommentId > 0) current.replyToUserId.takeIf { it > 0 }
            else articles.findById(current.articleId)?.authorId
        if (recipient != null) {
            commentNotices.createIfNeeded(
                recipientId = recipient,
                senderId = current.authorId,
                articleId = current.articleId,
                commentId = commentId,
                type = if (current.rootCommentId > 0) CommentNoticeType.REPLY else CommentNoticeType.COMMENT,
                now = now,
            )
        }
    }

    private fun rejectResource(row: ContentReviewRecord) {
        val id = row.resourceId?.toLongOrNull() ?: return
        val now = LocalDateTime.now()
        when (row.resourceType) {
            "ARTICLE" -> {
                if (articles.rejectDraft(id, now)) articleContents.markDeleted(id, now)
            }
            "COMMENT" -> {
                val current = comments.find(id) ?: return
                if (current.deletedAt == null && current.status == CommentStatus.PendingReview) {
                    comments.markDeleted(id, now)
                }
            }
        }
    }

    private fun Map<String, Any?>.string(key: String) = this[key]?.toString()
    private fun Map<String, Any?>.long(key: String) = this[key]?.toString()?.toLongOrNull()
}

private fun CommentStatisticRepository.createStatisticsIfAbsent(commentId: Long) {
    if (findStatisticsByIds(listOf(commentId)).none { it.commentId == commentId }) createStatistics(commentId)
}