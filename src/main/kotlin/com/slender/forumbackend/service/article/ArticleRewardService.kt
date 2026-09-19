package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.article.ArticleRewardValidator
import com.slender.forumbackend.exception.MoePointNotEnoughException
import com.slender.forumbackend.model.data.article.ArticleRewardData
import com.slender.forumbackend.model.entity.article.relation.ArticleReward
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleRewardRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.user.UserPointsRepository
import com.slender.forumbackend.configuration.BUSINESS_ZONE
import java.time.Clock
import org.springframework.stereotype.Service
import com.slender.forumbackend.exception.InvalidRequestException

@Service
class ArticleRewardService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleRewardRepository: ArticleRewardRepository,
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val pointsRepository: UserPointsRepository,
    private val clock: Clock,
    private val articleRewardValidator: ArticleRewardValidator,
) {
    fun reward(articleId: Long, userId: Long, amount: Int): ArticleRewardData {
        val article = articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        if (amount <= 0) throw InvalidRequestException("Reward amount must be positive")
        val balances = pointsRepository.lockBalances(listOf(userId, article.authorId))
        articleRewardValidator.validate(article, userId)
        if (balances.getValue(userId) < amount) throw MoePointNotEnoughException()
        val now = clock.instant().atZone(BUSINESS_ZONE).toLocalDateTime()
        val remaining = pointsRepository.add(userId, -amount.toLong())
        val authorBalance = pointsRepository.add(article.authorId, amount.toLong())
        articleRewardRepository.insertReward(
            ArticleReward(
                articleId = articleId,
                userId = userId,
                amount = amount,
                createTime = now,
            )
        )
        val key = "article-reward:$articleId:$userId"
        pointsRepository.record(userId, -amount.toLong(), remaining, "ARTICLE_REWARD_SENT", now, key)
        pointsRepository.record(article.authorId, amount.toLong(), authorBalance, "ARTICLE_REWARD_RECEIVED", now, key)
        articleStatisticRepository.incrementRewardCount(articleId, amount)
        val rewardCount = articleStatisticRepository
            .findStatisticsByArticleIds(listOf(articleId))
            .firstOrNull()?.rewardCount ?: error("Missing article statistics: $articleId")
        return ArticleRewardData(rewardCount, remainingMoePoint = remaining)
    }
}
