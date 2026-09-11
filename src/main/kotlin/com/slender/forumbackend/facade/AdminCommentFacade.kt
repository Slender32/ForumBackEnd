package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.CommentCreateRequest
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.service.admin.AdminCommentQueryService
import com.slender.forumbackend.service.comment.CommentCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCommentFacade(
    private val query: AdminCommentQueryService,
    private val commands: CommentCommandService,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean, articleId: Long?, authorId: Long?) =
        query.list(page, size, includeDeleted, articleId, authorId)

    fun get(id: Long) = query.get(id)

    @Transactional
    fun update(id: Long, operatorId: Long, authorities: Set<String>, request: CommentCreateRequest): Comment {
        commands.updateComment(id, operatorId, authorities, request.content)
        return query.get(id)
    }

    @Transactional
    fun delete(id: Long, operatorId: Long, authorities: Set<String>) =
        commands.deleteComment(id, operatorId, authorities)
}
