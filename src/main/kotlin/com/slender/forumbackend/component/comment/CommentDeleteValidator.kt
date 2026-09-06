package com.slender.forumbackend.component.comment

import com.slender.forumbackend.exception.CommentForbiddenException
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.repository.RbacRepository
import org.springframework.stereotype.Component

@Component
class CommentDeleteValidator(
    private val rbacRepository: RbacRepository,
) {
    fun validate(comment: Comment, userId: Long) {
        val canDeleteAny = rbacRepository.findAuthoritiesByUserId(userId).contains(DELETE_ANY_PERMISSION)
        if (comment.authorId != userId && !canDeleteAny) throw CommentForbiddenException()
    }

    private companion object {
        const val DELETE_ANY_PERMISSION = "comment:delete:any"
    }
}
