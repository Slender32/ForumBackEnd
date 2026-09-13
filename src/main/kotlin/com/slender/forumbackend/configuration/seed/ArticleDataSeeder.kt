package com.slender.forumbackend.configuration.seed

import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility
import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import com.slender.forumbackend.mapper.ArticleContentMapper
import com.slender.forumbackend.mapper.ArticleLikeMapper
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.mapper.ArticlePromotionMapper
import com.slender.forumbackend.mapper.ArticleReactionMapper
import com.slender.forumbackend.mapper.ArticleStatisticMapper
import com.slender.forumbackend.mapper.ArticleTagMapper
import com.slender.forumbackend.mapper.CarouselMapper
import com.slender.forumbackend.mapper.CommentLikeMapper
import com.slender.forumbackend.mapper.CommentMapper
import com.slender.forumbackend.mapper.CommentStatisticsMapper
import com.slender.forumbackend.mapper.TagMapper
import com.slender.forumbackend.model.entity.Tag
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleLike
import com.slender.forumbackend.model.entity.article.relation.ArticleTag
import com.slender.forumbackend.model.entity.carousel.Carousel
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.comment.relation.CommentLike
import java.time.LocalDateTime
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("dev")
@Component
class ArticleDataSeeder(
    private val tagMapper: TagMapper,
    private val articleMapper: ArticleMapper,
    private val articleContentMapper: ArticleContentMapper,
    private val articleStatisticMapper: ArticleStatisticMapper,
    private val articleTagMapper: ArticleTagMapper,
    private val articlePromotionMapper: ArticlePromotionMapper,
    private val articleLikeMapper: ArticleLikeMapper,
    private val articleReactionMapper: ArticleReactionMapper,
    private val commentMapper: CommentMapper,
    private val commentStatisticsMapper: CommentStatisticsMapper,
    private val commentLikeMapper: CommentLikeMapper,
    private val carouselMapper: CarouselMapper,
) {
    fun seed(now: LocalDateTime, users: SeedUserIds): SeedArticleIds {
        val tags = seedTags(now)
        val articles = seedArticles(now, users, tags)
        seedComments(now, users, articles)
        seedCarousels(now, articles)
        return articles
    }

    private fun seedTags(now: LocalDateTime): SeedTagIds =
        SeedTagIds(
            kotlin = seedTag("Kotlin", opaqueArgb(0x356DF3), now),
            frontend = seedTag("Frontend", opaqueArgb(0xFF8A00), now),
            backend = seedTag("Backend", opaqueArgb(0x1C9C5B), now),
            release = seedTag("Release", opaqueArgb(0xA74CFF), now),
            debug = seedTag("Debugging", opaqueArgb(0xE83E8C), now),
        )

    private fun opaqueArgb(rgb: Int): Int = 0xFF000000.toInt() or (rgb and 0x00FFFFFF)

    private fun seedTag(name: String, color: Int, now: LocalDateTime): Long {
        val tag = Tag(name = name, color = color, createTime = now)
        tagMapper.insert(tag)
        return tag.tid.requireGeneratedId("tag", name)
    }

    private fun seedArticles(
        now: LocalDateTime,
        users: SeedUserIds,
        tags: SeedTagIds,
    ): SeedArticleIds {
        val articleIds =
            listOf(
                    ArticleSeed(
                        authorId = users.alice,
                        title = "Article Home Chain Notes",
                        summary =
                            "Trace the article feed from UI to repository without losing the state transitions.",
                        cover = "https://picsum.photos/seed/article-home-chain/800/450",
                        publishTime = now.minusHours(2),
                        tags = listOf(tags.kotlin, tags.backend, tags.debug),
                        stats =
                            ArticleStatisticSeed(
                                likeCount = 3,
                                commentCount = 3,
                                viewCount = 1240,
                                rewardCount = 1,
                            ),
                        promotions =
                            listOf(
                                PromotionSeed(
                                    promoterId = users.admin,
                                    content =
                                        "A good starting point when you need to trace the feed end to end.",
                                    promoteTime = now.minusHours(1).minusMinutes(10),
                                )
                            ),
                        likes = listOf(users.dave, users.bob, users.carol),
                        reactions =
                            listOf(
                                ReactionSeed(
                                    users.dave,
                                    "\uD83D\uDC4D",
                                    now.minusHours(1).minusMinutes(40),
                                ),
                                ReactionSeed(
                                    users.bob,
                                    "\uD83D\uDC4D",
                                    now.minusHours(1).minusMinutes(25),
                                ),
                                ReactionSeed(
                                    users.carol,
                                    "\uD83D\uDD25",
                                    now.minusHours(1).minusMinutes(20),
                                ),
                            ),
                        content = ARTICLE_HOME_CHAIN_CONTENT,
                    ),
                    ArticleSeed(
                        authorId = users.bob,
                        title = "Frontend State Flow Checklist",
                        summary =
                            "A compact list for keeping refresh, load more, and navigation in sync.",
                        cover = "https://picsum.photos/seed/frontend-state-flow/800/450",
                        publishTime = now.minusHours(4),
                        tags = listOf(tags.frontend, tags.debug),
                        stats =
                            ArticleStatisticSeed(
                                likeCount = 2,
                                commentCount = 2,
                                viewCount = 860,
                                rewardCount = 0,
                            ),
                        promotions =
                            listOf(
                                PromotionSeed(
                                    promoterId = users.alice,
                                    content =
                                        "Useful when you need to reason about snapshot flow and refresh loops.",
                                    promoteTime = now.minusHours(3).minusMinutes(15),
                                )
                            ),
                        likes = listOf(users.dave, users.alice),
                        reactions =
                            listOf(
                                ReactionSeed(
                                    users.alice,
                                    "\uD83D\uDC4F",
                                    now.minusHours(3).minusMinutes(30),
                                ),
                                ReactionSeed(
                                    users.dave,
                                    "\uD83D\uDC4F",
                                    now.minusHours(3).minusMinutes(10),
                                ),
                            ),
                        content = STATE_FLOW_CONTENT,
                    ),
                    ArticleSeed(
                        authorId = users.carol,
                        title = "Release Week Cut List",
                        summary =
                            "A short release checklist for pages, counts, and demo data that must not drift.",
                        cover = "https://picsum.photos/seed/release-week-cut/800/450",
                        publishTime = now.minusDays(1).plusHours(2),
                        tags = listOf(tags.release, tags.frontend),
                        stats =
                            ArticleStatisticSeed(
                                likeCount = 1,
                                commentCount = 1,
                                viewCount = 410,
                                rewardCount = 0,
                            ),
                        promotions =
                            listOf(
                                PromotionSeed(
                                    promoterId = users.bob,
                                    content =
                                        "Small, direct, and useful for a final pass before shipping.",
                                    promoteTime = now.minusDays(1).plusHours(3),
                                )
                            ),
                        likes = listOf(users.dave),
                        reactions =
                            listOf(
                                ReactionSeed(
                                    users.dave,
                                    "\u26A1",
                                    now.minusDays(1).plusHours(1).minusMinutes(20),
                                )
                            ),
                        content = RELEASE_WEEK_CONTENT,
                    ),
                    ArticleSeed(
                        authorId = users.alice,
                        title = "Backend Demo Data Playbook",
                        summary =
                            "Build data that feels real, can be reset, and covers the critical UI states.",
                        cover = "https://picsum.photos/seed/backend-demo-playbook/800/450",
                        publishTime = now.minusDays(2),
                        tags = listOf(tags.backend, tags.debug, tags.kotlin),
                        stats =
                            ArticleStatisticSeed(
                                likeCount = 4,
                                commentCount = 2,
                                viewCount = 1500,
                                rewardCount = 2,
                            ),
                        promotions =
                            listOf(
                                PromotionSeed(
                                    promoterId = users.carol,
                                    content =
                                        "A good reference when you need backend-returned fake data fast.",
                                    promoteTime = now.minusDays(2).plusHours(1),
                                )
                            ),
                        likes = listOf(users.dave, users.bob, users.carol, users.admin),
                        reactions =
                            listOf(
                                ReactionSeed(
                                    users.dave,
                                    "\u2764\uFE0F",
                                    now.minusDays(2).plusMinutes(20),
                                ),
                                ReactionSeed(
                                    users.bob,
                                    "\u2764\uFE0F",
                                    now.minusDays(2).plusMinutes(40),
                                ),
                                ReactionSeed(
                                    users.admin,
                                    "\uD83D\uDE80",
                                    now.minusDays(2).plusMinutes(50),
                                ),
                            ),
                        content = DEMO_PLAYBOOK_CONTENT,
                    ),
                )
                .map { seed ->
                    val article =
                        Article(
                            authorId = seed.authorId,
                            title = seed.title,
                            summary = seed.summary,
                            cover = seed.cover,
                            status = ArticleStatus.Published,
                            visibility = ArticleVisibility.Public,
                            publishTime = seed.publishTime,
                            reviseTime = seed.publishTime,
                            createTime = seed.publishTime,
                            updateTime = seed.publishTime,
                        )
                    articleMapper.insert(article)
                    val articleId = article.articleId.requireGeneratedId("article", seed.title)

                    articleContentMapper.insert(ArticleContent(articleId, seed.content))
                    articleStatisticMapper.insert(
                        ArticleStatistic(
                            articleId = articleId,
                            likeCount = seed.stats.likeCount,
                            commentCount = seed.stats.commentCount,
                            viewCount = seed.stats.viewCount,
                            rewardCount = seed.stats.rewardCount,
                        )
                    )
                    seed.tags.forEach { tagId ->
                        articleTagMapper.insert(ArticleTag(articleId, tagId, seed.publishTime))
                    }
                    seed.promotions.forEach { promotion ->
                        articlePromotionMapper.insert(
                            ArticlePromotion(
                                articleId = articleId,
                                promoterId = promotion.promoterId,
                                content = promotion.content,
                                promoteTime = promotion.promoteTime,
                            )
                        )
                    }
                    seed.likes.forEach { userId ->
                        articleLikeMapper.insert(ArticleLike(articleId, userId, seed.publishTime))
                    }
                    seed.reactions.forEach { reaction ->
                        articleReactionMapper.upsertReaction(
                            articleId,
                            reaction.userId,
                            reaction.emoji,
                            reaction.createTime,
                        )
                    }

                    articleId
                }

        return SeedArticleIds(
            homeChain = articleIds[0],
            stateFlow = articleIds[1],
            releaseWeek = articleIds[2],
            demoPlaybook = articleIds[3],
        )
    }

    private fun seedComments(
        now: LocalDateTime,
        users: SeedUserIds,
        articles: SeedArticleIds,
    ): SeedCommentIds {
        val homeRoot =
            seedComment(
                CommentSeed(
                    articleId = articles.homeChain,
                    authorId = users.bob,
                    content = "This is the page I would open first when reviewing the feed.",
                    publishTime = now.minusMinutes(95),
                    likeCount = 3,
                    replyCount = 1,
                )
            )
        val homeReply =
            seedComment(
                CommentSeed(
                    articleId = articles.homeChain,
                    authorId = users.dave,
                    content = "Same here. The state flow is the part worth mapping first.",
                    publishTime = now.minusMinutes(88),
                    rootCommentId = homeRoot,
                    parentCommentId = homeRoot,
                    replyToUserId = users.bob,
                    likeCount = 0,
                    replyCount = 0,
                )
            )
        val homeSecond =
            seedComment(
                CommentSeed(
                    articleId = articles.homeChain,
                    authorId = users.carol,
                    content = "The list card feels stable once the cursor path is clear.",
                    publishTime = now.minusMinutes(80),
                    likeCount = 2,
                    replyCount = 0,
                )
            )
        val stateRoot =
            seedComment(
                CommentSeed(
                    articleId = articles.stateFlow,
                    authorId = users.alice,
                    content = "Snapshot flow is easier when the refresh path is isolated.",
                    publishTime = now.minusHours(3).minusMinutes(5),
                    likeCount = 4,
                    replyCount = 1,
                )
            )
        val stateReply =
            seedComment(
                CommentSeed(
                    articleId = articles.stateFlow,
                    authorId = users.dave,
                    content = "That keeps the UI from wandering into side effects.",
                    publishTime = now.minusHours(3).minusMinutes(2),
                    rootCommentId = stateRoot,
                    parentCommentId = stateRoot,
                    replyToUserId = users.alice,
                    likeCount = 1,
                    replyCount = 0,
                )
            )
        val releaseRoot =
            seedComment(
                CommentSeed(
                    articleId = articles.releaseWeek,
                    authorId = users.bob,
                    content = "Short enough to skim, which is exactly what a release list needs.",
                    publishTime = now.minusDays(1).plusHours(1).minusMinutes(10),
                    likeCount = 1,
                    replyCount = 0,
                )
            )
        val playbookRoot =
            seedComment(
                CommentSeed(
                    articleId = articles.demoPlaybook,
                    authorId = users.carol,
                    content = "This should cover the same states every time we start the app.",
                    publishTime = now.minusDays(2).plusMinutes(15),
                    likeCount = 2,
                    replyCount = 0,
                )
            )
        val playbookSecond =
            seedComment(
                CommentSeed(
                    articleId = articles.demoPlaybook,
                    authorId = users.dave,
                    content = "And it is easy to reset, which is the bit we keep forgetting.",
                    publishTime = now.minusDays(2).plusMinutes(25),
                    likeCount = 1,
                    replyCount = 0,
                )
            )

        listOf(
                CommentLike(homeRoot, users.dave, now.minusMinutes(60)),
                CommentLike(homeRoot, users.alice, now.minusMinutes(58)),
                CommentLike(homeRoot, users.carol, now.minusMinutes(56)),
                CommentLike(stateRoot, users.dave, now.minusHours(2).minusMinutes(20)),
                CommentLike(stateRoot, users.bob, now.minusHours(2).minusMinutes(18)),
                CommentLike(stateRoot, users.carol, now.minusHours(2).minusMinutes(16)),
                CommentLike(stateRoot, users.admin, now.minusHours(2).minusMinutes(14)),
                CommentLike(releaseRoot, users.dave, now.minusDays(1).plusHours(1).minusMinutes(5)),
                CommentLike(playbookRoot, users.alice, now.minusDays(2).plusMinutes(18)),
                CommentLike(playbookRoot, users.bob, now.minusDays(2).plusMinutes(20)),
                CommentLike(playbookSecond, users.dave, now.minusDays(2).plusMinutes(28)),
            )
            .forEach { commentLikeMapper.insert(it) }

        return SeedCommentIds(
            homeRoot = homeRoot,
            homeReply = homeReply,
            homeSecond = homeSecond,
            stateRoot = stateRoot,
            stateReply = stateReply,
            releaseRoot = releaseRoot,
            playbookRoot = playbookRoot,
            playbookSecond = playbookSecond,
        )
    }

    private fun seedComment(seed: CommentSeed): Long {
        val comment =
            Comment(
                articleId = seed.articleId,
                authorId = seed.authorId,
                rootCommentId = seed.rootCommentId,
                parentCommentId = seed.parentCommentId,
                replyToUserId = seed.replyToUserId,
                content = seed.content,
                status = CommentStatus.Normal,
                publishTime = seed.publishTime,
                createTime = seed.publishTime,
                updateTime = seed.publishTime,
            )
        commentMapper.insert(comment)
        val commentId = comment.commentId.requireGeneratedId("comment", seed.content.take(32))
        commentStatisticsMapper.insert(
            CommentStatistics(
                commentId = commentId,
                likeCount = seed.likeCount,
                replyCount = seed.replyCount,
            )
        )
        return commentId
    }

    private fun seedCarousels(now: LocalDateTime, articles: SeedArticleIds) {
        listOf(
                Carousel(
                    title = "Trace the article feed",
                    summary =
                        "Open the home page and follow the request all the way to the repository.",
                    image = "https://picsum.photos/seed/carousel-home-chain/1200/500",
                    targetType = CarouselTargetType.Article,
                    targetValue = articles.homeChain.toString(),
                    sortOrder = 1,
                    enabled = true,
                    createTime = now,
                    updateTime = now,
                ),
                Carousel(
                    title = "Review the state flow",
                    summary =
                        "A direct link to the page that explains refresh and load-more behavior.",
                    image = "https://picsum.photos/seed/carousel-state-flow/1200/500",
                    targetType = CarouselTargetType.Article,
                    targetValue = articles.stateFlow.toString(),
                    sortOrder = 2,
                    enabled = true,
                    createTime = now,
                    updateTime = now,
                ),
            )
            .forEach { carouselMapper.insert(it) }
    }

    private fun Long.requireGeneratedId(type: String, label: String): Long {
        require(this > 0) { "DemoDataSeeder failed to get generated $type id for $label" }
        return this
    }

    private data class ArticleStatisticSeed(
        val likeCount: Int,
        val commentCount: Int,
        val viewCount: Int,
        val rewardCount: Int,
    )

    private data class PromotionSeed(
        val promoterId: Long,
        val content: String,
        val promoteTime: LocalDateTime,
    )

    private data class ArticleSeed(
        val authorId: Long,
        val title: String,
        val summary: String,
        val cover: String,
        val publishTime: LocalDateTime,
        val tags: List<Long>,
        val stats: ArticleStatisticSeed,
        val promotions: List<PromotionSeed>,
        val likes: List<Long>,
        val reactions: List<ReactionSeed>,
        val content: String,
    )

    private data class ReactionSeed(
        val userId: Long,
        val emoji: String,
        val createTime: LocalDateTime,
    )

    private data class CommentSeed(
        val articleId: Long,
        val authorId: Long,
        val content: String,
        val publishTime: LocalDateTime,
        val rootCommentId: Long = 0,
        val parentCommentId: Long = 0,
        val replyToUserId: Long = 0,
        val likeCount: Int,
        val replyCount: Int,
    )

    private companion object {
        val FIXED_NOW: LocalDateTime = LocalDateTime.of(2026, 8, 19, 18, 30)

        val ARTICLE_HOME_CHAIN_CONTENT =
            """
                # Article home chain notes

                This seed article is for tracing the core feed path.

                ## Path
                - HomeMain
                - ArticleListService
                - ArticleController
                - ArticleQueryService
                - ArticleQueryRepository

                ## Goal
                Keep the chain readable, repeatable, and easy to reset.
            """
                .trimIndent()

        val STATE_FLOW_CONTENT =
            """
                # Frontend state flow checklist

                Use one trace for refresh, one for load more, and one for navigation.

                ## Rules
                - Keep side effects at the edge.
                - Reset filters before changing the route.
                - Seed data should always return the same shape.
            """
                .trimIndent()

        val RELEASE_WEEK_CONTENT =
            """
                # Release week cut list

                Focus on the data that makes the first screen trustworthy.

                ## Must stay
                - Home carousel
                - Article feed
                - Article detail
            """
                .trimIndent()

        val DEMO_PLAYBOOK_CONTENT =
            """
                # Backend demo data playbook

                Build fake data on the server so the frontend still exercises real controllers and services.

                ## Notes
                - Use generated IDs.
                - Reset by Docker.
                - Change content in one file.
            """
                .trimIndent()
    }
}
