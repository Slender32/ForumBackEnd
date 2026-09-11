package com.slender.forumbackend.repository.comment

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Deleted
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Normal
import com.slender.forumbackend.exception.CommentNotFoundException
import com.slender.forumbackend.mapper.CommentMapper
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class CommentQueryRepository(
    private val commentMapper: CommentMapper,
    private val articleQueryRepository: ArticleQueryRepository,
) : ServiceImpl<CommentMapper, Comment>(), IService<Comment> {
    private companion object {
        const val ROOT_COMMENT_NONE = 0L
    }

    fun findNormalRootByArticleIds(articleIds: List<Long>): List<Comment> =
        articleIds
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.let {
                commentMapper.selectNormalRootByArticleIds(it, Normal.value, ROOT_COMMENT_NONE)
            } ?: emptyList()

    fun findByIds(commentIds: List<Long>): List<Comment> =
        commentIds.distinct().takeIf { it.isNotEmpty() }?.let { commentMapper.selectByIds(it) }
            ?: emptyList()

    fun listAdmin(
        page: Int,
        size: Int,
        includeDeleted: Boolean = false,
        articleId: Long? = null,
        authorId: Long? = null,
    ): List<Comment> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        return commentMapper.selectList(
            QueryWrapper<Comment>()
                .apply(if (includeDeleted) "1=1" else "deleted_at IS NULL AND status <> 'DELETED'")
                .eq(articleId != null, "article_id", articleId)
                .eq(authorId != null, "author_id", authorId)
                .orderByDesc("create_time")
                .orderByDesc("comment_id")
                .last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        )
    }

    fun countAdmin(
        includeDeleted: Boolean = false,
        articleId: Long? = null,
        authorId: Long? = null,
    ): Long =
        commentMapper.selectCount(
            QueryWrapper<Comment>()
                .apply(if (includeDeleted) "1=1" else "deleted_at IS NULL AND status <> 'DELETED'")
                .eq(articleId != null, "article_id", articleId)
                .eq(authorId != null, "author_id", authorId)
        )

    fun findNormalCommentById(commentId: Long): Comment? =
        commentMapper.selectNormalById(commentId, Normal.value)

    fun findVisibleCommentById(commentId: Long): Comment? {
        val comment = findNormalCommentById(commentId) ?: return null
        if (!articleQueryRepository.targetExists(comment.articleId)) return null
        return comment
    }

    fun findVisibleCommentByIdOrThrow(commentId: Long): Comment =
        findVisibleCommentById(commentId) ?: throw CommentNotFoundException()

    fun findTopLevelComments(
        articleId: Long,
        cursorCommentId: Long,
        cursorPublishTime: LocalDateTime?,
        size: Int,
        sort: String,
    ): List<Comment> =
        commentMapper.selectTopLevelComments(
            articleId = articleId,
            rootCommentId = ROOT_COMMENT_NONE,
            cursorCommentId = cursorCommentId,
            cursorPublishTime = cursorPublishTime,
            limit = size + 1,
            sort = sort,
            normalStatus = Normal.value,
            deletedStatus = Deleted.value,
        )

    fun findRepliesByRootId(
        rootCommentId: Long,
        cursorReplyId: Long,
        cursorPublishTime: LocalDateTime?,
        size: Int,
    ): List<Comment> =
        commentMapper.selectRepliesByRootId(
            rootCommentId = rootCommentId,
            cursorReplyId = cursorReplyId,
            cursorPublishTime = cursorPublishTime,
            limit = size + 1,
            normalStatus = Normal.value,
            deletedStatus = Deleted.value,
        )

    fun findReplyPreviews(rootCommentIds: Collection<Long>, limit: Int): Map<Long, List<Comment>> {
        val ids = rootCommentIds.distinct()
        if (ids.isEmpty()) return emptyMap()
        return commentMapper.selectReplyPreviews(
            ids, limit,
            Normal.value, Deleted.value
        ).groupBy { it.rootCommentId }
    }

    fun findByAuthor(
        authorId: Long,
        cursorCommentId: Long,
        cursorPublishTime: LocalDateTime?,
        limit: Int,
    ): List<Comment> =
        commentMapper.selectByAuthor(
            authorId = authorId,
            cursorCommentId = cursorCommentId,
            cursorPublishTime = cursorPublishTime,
            limit = limit,
            normalStatus = Normal.value,
        )
}
