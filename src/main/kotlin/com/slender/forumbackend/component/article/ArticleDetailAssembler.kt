package com.slender.forumbackend.component.article

import com.slender.forumbackend.exception.ArticleNotFoundException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticleDetailData
import com.slender.forumbackend.model.data.article.ArticleReactionData
import com.slender.forumbackend.model.data.article.ArticleTagData
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.repository.article.ArticleInteractionRepository
import com.slender.forumbackend.repository.article.ArticleRewardRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import com.slender.forumbackend.repository.FavoriteRepository
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import org.springframework.stereotype.Component

@Component
class ArticleDetailAssembler(
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val articleTagRepository: ArticleTagRepository,
    private val articleInteractionRepository: ArticleInteractionRepository,
    private val articleRewardRepository: ArticleRewardRepository,
    private val tagRepository: TagRepository,
    private val userReadRepository: UserReadRepository,
    private val favoriteRepository: FavoriteRepository,
    private val articleViewRecorder: ArticleViewRecorder,
) {
    fun assemble(
        article: Article,
        content: ArticleContent,
        currentUserId: Long?,
        viewerKey: String,
    ): ArticleDetailData {
        articleViewRecorder.record(article.articleId, viewerKey)

        val statistics = articleStatisticRepository.findStatisticsByArticleIds(listOf(article.articleId))
            .firstOrNull()
        val articleTags = articleTagRepository.findTagsByArticleIds(listOf(article.articleId))
        val tags = tagRepository.findByIds(articleTags.map { it.tagId })
            .map { ArticleTagData(it.tid, it.name, it.color) }
        val author = userReadRepository.findById(article.authorId) ?: throw ArticleNotFoundException()
        val reactions = articleInteractionRepository.findReactionsByArticleIds(listOf(article.articleId))
            .toReactionData(currentUserId)
        val isLike = currentUserId?.let {
            articleInteractionRepository.hasLike(article.articleId, it)
        } ?: false
        val isRewarded = currentUserId?.let {
            articleRewardRepository.findReward(article.articleId, it) != null
        } ?: false
        val isFavorite = currentUserId?.let {
            favoriteRepository.find(it, article.articleId) != null
        } ?: false

        return ArticleDetailData(
            articleId = article.articleId,
            title = article.title,
            content = content.content,
            author = author.toArticleUserData(),
            tags = tags,
            publishTime = article.publishTime.timestamp,
            reviseTime = article.reviseTime.timestamp,
            viewCount = statistics?.viewCount ?: 0,
            likeCount = statistics?.likeCount ?: 0,
            rewardCount = statistics?.rewardCount ?: 0,
            commentCount = statistics?.commentCount ?: 0,
            reactions = reactions,
            isLike = isLike,
            isRewarded = isRewarded,
            isFavorite = isFavorite,
        )
    }

    private fun List<ArticleReaction>.toReactionData(currentUserId: Long?): List<ArticleReactionData> =
        groupBy { it.emoji }
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, List<ArticleReaction>>> { it.value.size }
                .thenBy { it.key })
            .take(REACTION_LIMIT)
            .map { (emoji, reactionList) ->
                val users = userReadRepository.findByIds(reactionList.map { it.userId })
                ArticleReactionData(
                    emoji = emoji,
                    count = reactionList.size,
                    reactors = reactionList
                        .sortedByDescending { it.createTime }
                        .mapNotNull { reaction -> users.find { user -> user.uid == reaction.userId }?.avatar }
                        .take(REACTION_AVATAR_LIMIT),
                    isReact = currentUserId?.let { uid ->
                        reactionList.any { it.userId == uid }
                    } ?: false,
                )
            }

    private companion object {
        const val REACTION_LIMIT = 15
        const val REACTION_AVATAR_LIMIT = 5
    }
}
