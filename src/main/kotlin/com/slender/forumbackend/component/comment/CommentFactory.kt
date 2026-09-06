package com.slender.forumbackend.component.comment

import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Normal
import com.slender.forumbackend.model.entity.comment.content.Comment
import java.time.LocalDateTime
import org.springframework.stereotype.Component

@Component
class CommentFactory {
    fun createRoot(articleId: Long, authorId: Long, content: String, now: LocalDateTime): Comment =
        Comment(
            0,
            articleId,
            authorId,
            0,
            0,
            0,
            content,
            Normal,
            now,
            now,
            now
        )

    fun createReply(parent: Comment, authorId: Long, content: String, now: LocalDateTime): Comment {
        val rootCommentId = parent.rootCommentId.takeIf { it != 0L } ?: parent.commentId
        return Comment(
            0,
            parent.articleId,
            authorId,
            rootCommentId,
            parent.commentId,
            parent.authorId,
            content,
            Normal,
            now,
            now,
            now
        )
    }
}
