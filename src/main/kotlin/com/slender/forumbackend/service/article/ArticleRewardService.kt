package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.article.ArticleRewardValidator
import com.slender.forumbackend.exception.MoePointNotEnoughException
import com.slender.forumbackend.model.data.article.ArticleRewardData
import com.slender.forumbackend.model.entity.article.relation.ArticleReward
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.article.ArticleRewardRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.user.UserReadRepository
import com.slender.forumbackend.repository.user.UserWriteRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class ArticleRewardService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleRewardRepository: ArticleRewardRepository,
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
    private val articleRewardValidator: ArticleRewardValidator,
) {
    fun reward(articleId: Long, userId: Long, amount: Int): ArticleRewardData {
        val article = articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        articleRewardValidator.validate(article, userId)
        val isSufficient = userWriteRepository.deductMoePoint(userId, amount)
        if (!isSufficient) throw MoePointNotEnoughException()
        userWriteRepository.addMoePoint(article.authorId, amount)
        articleRewardRepository.insertReward(
            ArticleReward(
                articleId = articleId,
                userId = userId,
                amount = amount,
                createTime = now(),
            )
        )
        articleStatisticRepository.incrementRewardCount(articleId, amount)
        val rewardCount = articleStatisticRepository
            .findStatisticsByArticleIds(listOf(articleId))
            .firstOrNull()?.rewardCount ?: amount
        val remaining = userReadRepository.findStatisticsById(userId)?.moePoint ?: 0
        return ArticleRewardData(rewardCount, remainingMoePoint = remaining)
    }
}
