package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleLike
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.model.entity.article.relation.ArticleTag
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
interface ArticleMapper : BaseMapper<Article>

@Mapper
interface ArticleContentMapper : BaseMapper<ArticleContent>

@Mapper
interface ArticleStatisticMapper : BaseMapper<ArticleStatistic>

@Mapper
interface ArticlePromotionMapper : BaseMapper<ArticlePromotion>

@Mapper
interface ArticleLikeMapper : BaseMapper<ArticleLike>

@Mapper
interface ArticleReactionMapper : BaseMapper<ArticleReaction>

@Mapper
interface ArticleTagMapper : BaseMapper<ArticleTag>

@Repository
class ArticleRepository(
    private val articleMapper: ArticleMapper,
    private val articleContentMapper: ArticleContentMapper,
    private val articleStatisticMapper: ArticleStatisticMapper,
    private val articlePromotionMapper: ArticlePromotionMapper,
    private val articleLikeMapper: ArticleLikeMapper,
    private val articleReactionMapper: ArticleReactionMapper,
    private val articleTagMapper: ArticleTagMapper,
)
