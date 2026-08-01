package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.mapper.ArticleRepository
import com.slender.forumbackend.mapper.CommentRepository
import com.slender.forumbackend.mapper.TagRepository
import com.slender.forumbackend.mapper.UserRepository
import com.slender.forumbackend.model.article.ArticleCommentData
import com.slender.forumbackend.model.article.ArticleContentData
import com.slender.forumbackend.model.article.ArticleItemData
import com.slender.forumbackend.model.article.ArticleListData
import com.slender.forumbackend.model.article.ArticlePromotionData
import com.slender.forumbackend.model.article.ArticleReactionData
import com.slender.forumbackend.model.article.ArticleTagData
import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.entity.Tag
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.user.content.User
import org.springframework.stereotype.Service
import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.ZoneOffset.ofHours

interface ArticleService : IService<Article> {
    fun list(request: ArticleListRequest): ArticleListData
    fun content(articleId: Long): ArticleContentData
}

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
) : ArticleService, ServiceImpl<ArticleMapper, Article>() {

    private companion object {
        const val TAG_LIMIT = 10
        const val PROMOTION_LIMIT = 10
        const val COMMENT_LIMIT = 5
        const val REACTION_LIMIT = 15
        const val REACTION_AVATAR_LIMIT = 5

        fun Long.toLocalDateTime(): LocalDateTime = ofEpochMilli(this).atZone(ofHours(8)).toLocalDateTime()
    }

    override fun list(request: ArticleListRequest): ArticleListData {
        val fetchedArticles = articleRepository.findVisibleArticlePage(
            request.cursorPublishTime?.toLocalDateTime(),
            request.cursorArticleId,
            request.size + 1,
        )
        val hasMore = fetchedArticles.size > request.size
        val articles = fetchedArticles.take(request.size)
        val bundle = findListBundle(articles)

        return ArticleListData(
            items = articles.map { it.toItemData(bundle) },
            nextCursor = if (hasMore) articles.lastOrNull()?.toCursorData() else null,
            hasMore = hasMore,
        )
    }

    override fun content(articleId: Long): ArticleContentData {
        articleRepository.findVisibleArticleById(articleId) ?: throw ArticleNotFoundException()
        val content = articleRepository.findContentById(articleId) ?: throw ArticleNotFoundException()
        return ArticleContentData(content.content)
    }

    private final fun findListBundle(articles: List<Article>): ArticleListBundle {
        if (articles.isEmpty()) return ArticleListBundle()

        val articleIds = articles.map { it.articleId }
        val statisticsByArticleId = articleRepository.findStatisticsByArticleIds(articleIds)
            .associateBy { it.articleId }
        val articleTags = articleRepository.findTagsByArticleIds(articleIds)
        val tagsById = tagRepository.findByIds(articleTags.map { it.tagId })
            .associateBy { it.tid }
        val promotions = articleRepository.findPromotionsByArticleIds(articleIds)
        val comments = commentRepository.findNormalRootByArticleIds(articleIds)
        val commentStatisticsById = commentRepository.findStatisticsByIds(comments.map { it.commentId })
            .associateBy { it.commentId }
        val reactions = articleRepository.findReactionsByArticleIds(articleIds)
        val usersById = userRepository.findByIds(
            articles.map { it.authorId } +
                promotions.map { it.promoterId } +
                comments.map { it.authorId } +
                reactions.map { it.userId }
        ).associateBy { it.uid }

        return ArticleListBundle(
            usersById = usersById,
            statisticsByArticleId = statisticsByArticleId,
            tagsByArticleId = articleTags
                .groupBy { it.articleId }
                .mapValues { (_, tags) -> tags.mapNotNull { tagsById[it.tagId] } },
            promotionsByArticleId = promotions.groupBy { it.articleId },
            commentsByArticleId = comments.groupBy { it.articleId },
            commentStatisticsById = commentStatisticsById,
            reactionsByArticleId = reactions.groupBy { it.articleId },
        )
    }

    private final fun Article.toItemData(bundle: ArticleListBundle): ArticleItemData {
        val statistics = bundle.statisticsByArticleId[articleId]
        return ArticleItemData(
            articleId = articleId,
            author = bundle.usersById[authorId]?.toArticleUserData() ?: throw ArticleNotFoundException(),
            publishTime = publishTime.timestamp,
            reviseTime = reviseTime.timestamp,
            title = title,
            summary = summary,
            cover = cover,
            likeCount = statistics?.likeCount ?: 0,
            commentCount = statistics?.commentCount ?: 0,
            viewCount = statistics?.viewCount ?: 0,
            tags = bundle.tagsByArticleId[articleId].orEmpty()
                .take(TAG_LIMIT)
                .map { ArticleTagData(it.tid, it.name) },
            promotions = bundle.promotionsByArticleId[articleId].orEmpty()
                .take(PROMOTION_LIMIT)
                .mapNotNull { promotion ->
                    val promoter = bundle.usersById[promotion.promoterId] ?: return@mapNotNull null
                    ArticlePromotionData(
                        promotionId = promotion.promotionId,
                        promoter = promoter.toArticleUserData(),
                        promoteTime = promotion.promoteTime.timestamp,
                        content = promotion.content,
                    )
                },
            comments = bundle.commentsByArticleId[articleId].orEmpty()
                .sortedWith(
                    compareByDescending<Comment> {
                        bundle.commentStatisticsById[it.commentId]?.likeCount ?: 0
                    }.thenByDescending { it.publishTime }
                )
                .take(COMMENT_LIMIT)
                .mapNotNull { comment ->
                    val author = bundle.usersById[comment.authorId] ?: return@mapNotNull null
                    ArticleCommentData(
                        commentId = comment.commentId,
                        author = author.toArticleUserData(),
                        publishTime = comment.publishTime.timestamp,
                        content = comment.content,
                        likeCount = bundle.commentStatisticsById[comment.commentId]?.likeCount ?: 0,
                    )
                },
            reactions = bundle.reactionsByArticleId[articleId].orEmpty()
                .groupBy { it.emoji }
                .entries
                .sortedWith(compareByDescending<Map.Entry<String, List<ArticleReaction>>> { it.value.size }
                    .thenBy { it.key })
                .take(REACTION_LIMIT)
                .map { (emoji, reactions) ->
                    ArticleReactionData(
                        emoji = emoji,
                        count = reactions.size,
                        reactors = reactions
                            .sortedByDescending { it.createTime }
                            .mapNotNull { bundle.usersById[it.userId]?.avatar }
                            .take(REACTION_AVATAR_LIMIT),
                    )
                },
        )
    }
}

private data class ArticleListBundle(
    val usersById: Map<Long, User> = emptyMap(),
    val statisticsByArticleId: Map<Long, ArticleStatistic> = emptyMap(),
    val tagsByArticleId: Map<Long, List<Tag>> = emptyMap(),
    val promotionsByArticleId: Map<Long, List<ArticlePromotion>> = emptyMap(),
    val commentsByArticleId: Map<Long, List<Comment>> = emptyMap(),
    val commentStatisticsById: Map<Long, CommentStatistics> = emptyMap(),
    val reactionsByArticleId: Map<Long, List<ArticleReaction>> = emptyMap(),
)
