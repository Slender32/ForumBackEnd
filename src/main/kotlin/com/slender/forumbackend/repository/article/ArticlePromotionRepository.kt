package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticlePromotionMapper
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import org.springframework.stereotype.Repository

@Repository
class ArticlePromotionRepository(
    private val articlePromotionMapper: ArticlePromotionMapper,
) : ServiceImpl<ArticlePromotionMapper, ArticlePromotion>(), IService<ArticlePromotion> {
    fun findPromotionsByArticleIds(articleIds: Collection<Long>) =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            articlePromotionMapper.selectByArticleIds(it)
        } ?: emptyList()
}
