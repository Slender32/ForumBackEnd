package com.slender.forumbackend.component.article

import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticleCommentData
import com.slender.forumbackend.model.data.article.ArticleItemData
import com.slender.forumbackend.model.data.article.ArticlePromotionData
import com.slender.forumbackend.model.data.article.ArticleReactionData
import com.slender.forumbackend.model.data.article.ArticleTagData
import com.slender.forumbackend.model.entity.Tag
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.user.content.User
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.repository.article.ArticlePromotionRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import com.slender.forumbackend.repository.comment.CommentInteractionRepository
import com.slender.forumbackend.repository.comment.CommentQueryRepository
import com.slender.forumbackend.repository.comment.CommentStatisticRepository
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Component

@Component
class ArticleListAssembler(
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val articleTagRepository: ArticleTagRepository,
    private val articlePromotionRepository: ArticlePromotionRepository,
    private val articleInteractionRepository: ArticleInteractionRepository,
    private val commentQueryRepository: CommentQueryRepository,
    private val commentStatisticRepository: CommentStatisticRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val tagRepository: TagRepository,
    private val userReadRepository: UserReadRepository,
) {
    fun assemble(articles: List<Article>, currentUserId: Long?): List<ArticleItemData> {
        val bundle = findListBundle(articles, currentUserId)
        return articles.map { it.toItemData(bundle) }
    }

    private fun findListBundle(articles: List<Article>, currentUserId: Long?): ArticleListBundle {
        if (articles.isEmpty()) return ArticleListBundle()

        val articleIds = articles.map { it.articleId }
        val statisticsByArticleId = articleStatisticRepository.findStatisticsByArticleIds(articleIds)
            .associateBy { it.articleId }
        val articleTags = articleTagRepository.findTagsByArticleIds(articleIds)
        val tagsById = tagRepository.findByIds(articleTags.map { it.tagId })
            .associateBy { it.tid }
        val promotions = articlePromotionRepository.findPromotionsByArticleIds(articleIds)
        val comments = commentQueryRepository.findNormalRootByArticleIds(articleIds)
        val commentStatisticsById = commentStatisticRepository.findStatisticsByIds(comments.map { it.commentId })
            .associateBy { it.commentId }
        val reactions = articleInteractionRepository.findReactionsByArticleIds(articleIds)
        val usersById = userReadRepository.findByIds(
            articles.map { it.authorId } +
                promotions.map { it.promoterId } +
                comments.map { it.authorId } +
                reactions.map { it.userId }
        ).associateBy { it.uid }

        val likedArticleIds = currentUserId?.let {
            articleInteractionRepository.findLikesByUserAndArticleIds(it, articleIds)
        } ?: emptySet()

        val likedCommentIds = currentUserId?.let {
            val commentIds = comments.map { comment -> comment.commentId }
            commentInteractionRepository.findLikesByUserAndCommentIds(it, commentIds)
        } ?: emptySet()

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
            likedArticleIds = likedArticleIds,
            likedCommentIds = likedCommentIds,
        )
    }

    private fun Article.toItemData(bundle: ArticleListBundle): ArticleItemData {
        val statistics = bundle.statisticsByArticleId[articleId]

        return ArticleItemData(
            articleId = articleId,
            author = bundle.usersById[authorId]?.toArticleUserData()
                ?: throw ArticleNotFoundException("Article author not found: articleId=$articleId, authorId=$authorId"),
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
                .map { ArticleTagData(it.tid, it.name, it.color) },
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
                        isLike = bundle.likedCommentIds.contains(comment.commentId),
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
            isLike = bundle.likedArticleIds.contains(articleId),
        )
    }

    private companion object {
        const val TAG_LIMIT = 10
        const val PROMOTION_LIMIT = 10
        const val COMMENT_LIMIT = 5
        const val REACTION_LIMIT = 15
        const val REACTION_AVATAR_LIMIT = 5
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
    val likedArticleIds: Set<Long> = emptySet(),
    val likedCommentIds: Set<Long> = emptySet(),
)
