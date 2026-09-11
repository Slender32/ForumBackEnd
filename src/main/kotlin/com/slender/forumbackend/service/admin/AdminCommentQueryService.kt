package com.slender.forumbackend.service.admin

import com.slender.forumbackend.exception.CommentNotFoundException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import org.springframework.stereotype.Service

@Service
class AdminCommentQueryService(
    private val comments: CommentQueryRepository,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean, articleId: Long?, authorId: Long?): AdminPageData<Comment> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        return AdminPageData(
            comments.listAdmin(safePage, safeSize, includeDeleted, articleId, authorId),
            safePage,
            safeSize,
            comments.countAdmin(includeDeleted, articleId, authorId),
        )
    }

    fun get(id: Long): Comment = comments.findByIds(listOf(id)).firstOrNull() ?: throw CommentNotFoundException()
}
