package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.comment.relation.CommentLike
import com.slender.forumbackend.model.entity.comment.relation.CommentReaction
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

@Mapper
interface CommentMapper : BaseMapper<Comment> {
    fun selectNormalRootByArticleIds(
        @Param("articleIds") articleIds: Collection<Long>,
        @Param("normalStatus") normalStatus: String,
        @Param("rootCommentId") rootCommentId: Long,
    ): List<Comment>

    fun selectNormalById(
        @Param("commentId") commentId: Long,
        @Param("normalStatus") normalStatus: String,
    ): Comment?

    fun selectTopLevelComments(
        @Param("articleId") articleId: Long,
        @Param("rootCommentId") rootCommentId: Long,
        @Param("cursorCommentId") cursorCommentId: Long,
        @Param("cursorPublishTime") cursorPublishTime: LocalDateTime?,
        @Param("limit") limit: Int,
        @Param("sort") sort: String,
        @Param("normalStatus") normalStatus: String,
        @Param("deletedStatus") deletedStatus: String,
    ): List<Comment>

    fun selectRepliesByRootId(
        @Param("rootCommentId") rootCommentId: Long,
        @Param("cursorReplyId") cursorReplyId: Long,
        @Param("cursorPublishTime") cursorPublishTime: LocalDateTime?,
        @Param("limit") limit: Int,
        @Param("normalStatus") normalStatus: String,
        @Param("deletedStatus") deletedStatus: String,
    ): List<Comment>

    fun selectReplyPreviews(
        @Param("rootCommentIds") rootCommentIds: Collection<Long>,
        @Param("limit") limit: Int,
        @Param("normalStatus") normalStatus: String,
        @Param("deletedStatus") deletedStatus: String,
    ): List<Comment>

    fun selectByAuthor(
        @Param("authorId") authorId: Long,
        @Param("cursorCommentId") cursorCommentId: Long,
        @Param("cursorPublishTime") cursorPublishTime: LocalDateTime?,
        @Param("limit") limit: Int,
        @Param("normalStatus") normalStatus: String,
    ): List<Comment>

    fun countNormalByArticleId(
        @Param("articleId") articleId: Long,
        @Param("normalStatus") normalStatus: String,
    ): Long

    fun countNormalRepliesByRootId(
        @Param("rootCommentId") rootCommentId: Long,
        @Param("normalStatus") normalStatus: String,
    ): Long

    fun countNormalRepliesByRootIds(
        @Param("rootCommentIds") rootCommentIds: Collection<Long>,
        @Param("normalStatus") normalStatus: String,
    ): List<Map<String, Any>>
}

@Mapper
interface CommentStatisticsMapper : BaseMapper<CommentStatistics>

@Mapper
interface CommentLikeMapper : BaseMapper<CommentLike> {
    fun selectByCommentAndUser(
        @Param("commentId") commentId: Long,
        @Param("userId") userId: Long,
    ): CommentLike?

    fun selectCommentIdsByUser(
        @Param("userId") userId: Long,
        @Param("commentIds") commentIds: Collection<Long>,
    ): List<Long>

    fun deleteByCommentAndUser(
        @Param("commentId") commentId: Long,
        @Param("userId") userId: Long,
    ): Int
}

@Mapper
interface CommentReactionMapper : BaseMapper<CommentReaction> {
    fun selectByCommentIds(@Param("commentIds") commentIds: Collection<Long>): List<CommentReaction>

    fun upsertReaction(
        @Param("commentId") commentId: Long,
        @Param("userId") userId: Long,
        @Param("emoji") emoji: String,
        @Param("createTime") createTime: LocalDateTime,
    )
}
