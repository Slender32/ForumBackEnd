package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticleStatisticMapper
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import org.springframework.stereotype.Repository

@Repository
class ArticleStatisticRepository(
    private val articleStatisticMapper: ArticleStatisticMapper,
) : ServiceImpl<ArticleStatisticMapper, ArticleStatistic>(), IService<ArticleStatistic> {
    fun createStatistic(articleId: Long) {
        articleStatisticMapper.insert(ArticleStatistic(articleId))
    }

    fun findStatisticsByArticleIds(articleIds: Collection<Long>): List<ArticleStatistic> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let { articleStatisticMapper.selectByIds(it) } ?: emptyList()

    fun incrementCommentCount(articleId: Long, delta: Int) {
        val current = articleStatisticMapper.selectById(articleId)
        if (current != null) {
            val updated = current.copy(commentCount = (current.commentCount + delta).coerceAtLeast(0))
            articleStatisticMapper.updateById(updated)
        }
    }

    fun incrementViewCount(articleId: Long, delta: Int) {
        val current = articleStatisticMapper.selectById(articleId)
        if (current != null) {
            val updated = current.copy(viewCount = current.viewCount + delta)
            articleStatisticMapper.updateById(updated)
        }
    }

    fun incrementRewardCount(articleId: Long, amount: Int) {
        check(articleStatisticMapper.incrementRewardCount(articleId, amount) == 1) { "Missing article statistics: $articleId" }
    }
}
