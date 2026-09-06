package com.slender.forumbackend.service.user

import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Deleted as ArticleDeleted
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticleListData
import com.slender.forumbackend.model.data.comment.CommentCursorData
import com.slender.forumbackend.model.data.user.UserCommentItemData
import com.slender.forumbackend.model.data.user.UserCommentListData
import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.request.FavoriteListRequest
import com.slender.forumbackend.model.request.UserCommentListRequest
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.repository.comment.CommentStatisticRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.service.article.ArticleQueryService
import org.springframework.security.access.AccessDeniedException
import java.time.Instant.ofEpochMilli
import java.time.ZoneOffset.ofHours
import org.springframework.stereotype.Service

@Service
class UserContentQueryService(
    private val userReadRepository: UserReadRepository,
    private val articleQueryRepository: ArticleQueryRepository,
    private val commentQueryRepository: CommentQueryRepository,
    private val commentStatisticRepository: CommentStatisticRepository,
    private val articleQueryService: ArticleQueryService,
) {
    fun listArticles(uid: Long, request: ArticleListRequest, currentUserId: Long?): ArticleListData {
        userReadRepository.findByIdOrThrow(uid)
        return articleQueryService.listByAuthor(uid, request, currentUserId)
    }

    fun listComments(uid: Long, request: UserCommentListRequest): UserCommentListData {
        userReadRepository.findByIdOrThrow(uid)
        val cursorTime = request.cursorPublishTime?.let { ofEpochMilli(it).atZone(ofHours(8)).toLocalDateTime() }
        val fetched = commentQueryRepository.findByAuthor(
            authorId = uid,
            cursorCommentId = request.cursorCommentId,
            cursorPublishTime = cursorTime,
            limit = request.size + 1,
        )
        val hasMore = fetched.size > request.size
        val comments = fetched.take(request.size)
        val articles = articleQueryRepository.findByIds(comments.map { it.articleId }).associateBy { it.articleId }
        val replyToUserNames = userReadRepository.findByIds(
            comments.mapNotNull { it.replyToUserId.takeUnless { userId -> userId == ROOT_NONE } }
        ).associate { it.uid to it.name }
        val statistics = commentStatisticRepository.findStatisticsByIds(comments.map { it.commentId })
            .associateBy { it.commentId }
        return UserCommentListData(
            items = comments.map { comment ->
                val article = articles[comment.articleId]
                val visibleArticle = article?.takeUnless { it.status == ArticleDeleted }
                UserCommentItemData(
                    commentId = comment.commentId,
                    content = comment.content,
                    replyToUserName = replyToUserNames[comment.replyToUserId] ?: "",
                    publishTime = comment.publishTime.timestamp,
                    likeCount = statistics[comment.commentId]?.likeCount ?: 0,
                    articleId = visibleArticle?.articleId ?: -1L,
                    articleTitle = visibleArticle?.title ?: "文章已删除",
                )
            },
            nextCursor = if (hasMore) comments.lastOrNull()?.let {
                CommentCursorData(it.commentId, it.publishTime.timestamp)
            } else null,
            hasMore = hasMore,
        )
    }

    fun listFavorites(uid: Long, request: FavoriteListRequest, currentUserId: Long?): ArticleListData {
        userReadRepository.findByIdOrThrow(uid)
        if (currentUserId != uid) throw AccessDeniedException("NO_PERMISSION")
        return articleQueryService.listFavorites(uid, request, currentUserId)
    }

    private companion object {
        const val ROOT_NONE = 0L
    }
}
