package com.slender.forumbackend.service.comment

import com.slender.forumbackend.repository.comment.CommentInteractionRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class CommentInteractionService(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
) {
    fun toggleLike(commentId: Long, userId: Long) {
        commentQueryRepository.findVisibleCommentByIdOrThrow(commentId)
        if (commentInteractionRepository.hasLike(commentId, userId)) {
            commentInteractionRepository.deleteLike(commentId, userId)
            commentInteractionRepository.adjustLikeCount(commentId, -1)
        } else {
            commentInteractionRepository.insertLike(commentId, userId, LocalDateTime.now())
            commentInteractionRepository.adjustLikeCount(commentId, 1)
        }
    }
}