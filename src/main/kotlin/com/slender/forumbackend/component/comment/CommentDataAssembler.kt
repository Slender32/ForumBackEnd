package com.slender.forumbackend.component.comment

import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Deleted
import com.slender.forumbackend.exception.CommentNotFoundException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.comment.CommentData
import com.slender.forumbackend.model.data.comment.CommentReactionData
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.entity.comment.relation.CommentReaction
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.repository.comment.CommentInteractionRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.repository.comment.CommentStatisticRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Component

@Component
class CommentDataAssembler(
    private val commentQueryRepository: CommentQueryRepository,
    private val commentStatisticRepository: CommentStatisticRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val userReadRepository: UserReadRepository,
) {
    fun assemble(
        comments: List<Comment>,
        currentUserId: Long?,
        includeReplies: Boolean,
    ): List<CommentData> {
        if (comments.isEmpty()) return emptyList()

        val repliesByRoot = if (includeReplies) {
            commentQueryRepository.findReplyPreviews(
                comments.map { it.commentId },
                REPLY_PREVIEW_LIMIT,
            )
        } else {
            emptyMap()
        }

        val allComments = comments + repliesByRoot.values.flatten()
        val commentIds = allComments.map { it.commentId }
        val authorIds = allComments.flatMap { comment ->
            buildList {
                add(comment.authorId)
                if (comment.replyToUserId != ROOT_NONE) add(comment.replyToUserId)
            }
        }.toSet()

        val authors = userReadRepository.findByIds(authorIds).associateBy { it.uid }
        val statistics = commentStatisticRepository.findStatisticsByIds(commentIds)
            .associateBy { it.commentId }
        val replyCountsByRoot = commentStatisticRepository.countRepliesByRootIds(
            comments.filter { it.rootCommentId == ROOT_NONE }.map { it.commentId }
        )
        val likedCommentIds = currentUserId?.let {
            commentInteractionRepository.findLikesByUserAndCommentIds(it, commentIds)
        } ?: emptySet()
        val reactionsByCommentId = commentInteractionRepository.findReactionsByCommentIds(commentIds)
            .groupBy { it.commentId }
        val reactorIds = reactionsByCommentId.values.flatten().map { it.userId }.toSet()
        val reactors = userReadRepository.findByIds(reactorIds).associateBy { it.uid }

        fun toData(comment: Comment, nestedReplies: List<CommentData>): CommentData {
            val author = authors[comment.authorId] ?: throw CommentNotFoundException()
            val stat = statistics[comment.commentId]
            return CommentData(
                commentId = comment.commentId,
                articleId = comment.articleId,
                author = author.toArticleUserData(),
                publishTime = comment.publishTime.timestamp,
                updateTime = comment.updateTime.timestamp,
                content = if (comment.status == Deleted) DELETED_COMMENT_CONTENT else comment.content,
                rootCommentId = comment.rootCommentId.toSentinel(),
                parentCommentId = comment.parentCommentId.toSentinel(),
                replyToUserId = comment.replyToUserId.toSentinel(),
                replyToUserName = if (comment.replyToUserId != ROOT_NONE) {
                    authors[comment.replyToUserId]?.name ?: ""
                } else "",
                likeCount = stat?.likeCount ?: 0,
                replyCount = if (comment.rootCommentId == ROOT_NONE) {
                    replyCountsByRoot[comment.commentId] ?: 0
                } else 0,
                reactions = toReactionData(reactionsByCommentId[comment.commentId].orEmpty(), reactors, currentUserId),
                isLiked = likedCommentIds.contains(comment.commentId),
                replies = nestedReplies,
            )
        }

        return comments.map { comment ->
            val nested = repliesByRoot[comment.commentId].orEmpty().map { reply ->
                toData(reply, emptyList())
            }
            toData(comment, nested)
        }
    }

    private fun toReactionData(
        reactions: List<CommentReaction>,
        reactors: Map<Long, User>,
        currentUserId: Long?,
    ): List<CommentReactionData> =
        reactions
            .groupBy { it.emoji }
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, List<CommentReaction>>> { it.value.size }
                .thenBy { it.key })
            .map { (emoji, items) ->
                CommentReactionData(
                    emoji = emoji,
                    count = items.size,
                    reactors = items
                        .sortedByDescending { it.createTime }
                        .mapNotNull { reactors[it.userId]?.avatar }
                        .take(REACTION_AVATAR_LIMIT),
                    isReact = currentUserId?.let { uid -> items.any { it.userId == uid } } ?: false,
                )
            }

    private fun Long.toSentinel(): Long = if (this == ROOT_NONE) -1L else this

    private companion object {
        const val DELETED_COMMENT_CONTENT = "该评论已删除"
        const val ROOT_NONE = 0L
        const val REPLY_PREVIEW_LIMIT = 3
        const val REACTION_AVATAR_LIMIT = 3
    }
}
