package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.exception.ArticleAlreadyRewardedException
import com.slender.forumbackend.mapper.ArticleRewardMapper
import com.slender.forumbackend.model.entity.article.relation.ArticleReward
import org.springframework.stereotype.Repository
import org.springframework.dao.DuplicateKeyException

@Repository
class ArticleRewardRepository(
    private val articleRewardMapper: ArticleRewardMapper,
) : ServiceImpl<ArticleRewardMapper, ArticleReward>(), IService<ArticleReward> {
    fun findReward(articleId: Long, userId: Long): ArticleReward? =
        articleRewardMapper.selectByArticleAndUser(articleId, userId)

    fun insertReward(reward: ArticleReward) {
        try {
            articleRewardMapper.insert(reward)
        } catch (_: DuplicateKeyException) {
            throw ArticleAlreadyRewardedException()
        }
    }
}
