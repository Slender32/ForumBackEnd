package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Published
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility.Public
import com.slender.forumbackend.constant.field.ArticleField.ARTICLE_ID
import com.slender.forumbackend.constant.field.ArticleField.CREATE_TIME
import com.slender.forumbackend.constant.field.ArticleField.PROMOTE_TIME
import com.slender.forumbackend.constant.field.ArticleField.PUBLISH_TIME
import com.slender.forumbackend.constant.field.ArticleField.STATUS
import com.slender.forumbackend.constant.field.ArticleField.VISIBILITY
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleLike
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.model.entity.article.relation.ArticleTag
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

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
){
    fun findVisibleArticleById(articleId: Long): Article? =
        articleMapper.selectOne(
            QueryWrapper<Article>()
                .eq(ARTICLE_ID, articleId)
                .eq(STATUS, Published.value)
                .eq(VISIBILITY, Public.value)
        )

    fun findContentById(articleId: Long): ArticleContent? =
        articleContentMapper.selectById(articleId)

    fun findVisibleArticlePage(
        cursorPublishTime: LocalDateTime?,
        cursorArticleId: Long,
        limit: Int,
    ): List<Article> {
        val wrapper = QueryWrapper<Article>()
            .eq(STATUS, Published.value)
            .eq(VISIBILITY, Public.value)

        if (cursorPublishTime != null) {
            wrapper.apply(
                "(publish_time < {0} OR (publish_time = {0} AND article_id < {1}))",
                cursorPublishTime,
                cursorArticleId,
            )
        }

        return articleMapper.selectList(
            wrapper
                .orderByDesc(PUBLISH_TIME)
                .orderByDesc(ARTICLE_ID)
                .last("LIMIT $limit")
        )
    }

    fun findStatisticsByArticleIds(articleIds: Collection<Long>): List<ArticleStatistic> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let { articleStatisticMapper.selectByIds(it) } ?: emptyList()

    fun findTagsByArticleIds(articleIds: Collection<Long>): List<ArticleTag> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            articleTagMapper.selectList(
                QueryWrapper<ArticleTag>()
                    .`in`(ARTICLE_ID, it)
                    .orderByAsc(CREATE_TIME)
            )
        } ?: emptyList()

    fun findPromotionsByArticleIds(articleIds: Collection<Long>): List<ArticlePromotion> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            articlePromotionMapper.selectList(
                QueryWrapper<ArticlePromotion>()
                    .`in`(ARTICLE_ID, it)
                    .orderByDesc(PROMOTE_TIME)
            )
        } ?: emptyList()

    fun findReactionsByArticleIds(articleIds: Collection<Long>): List<ArticleReaction> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            articleReactionMapper.selectList(
                QueryWrapper<ArticleReaction>()
                    .`in`(ARTICLE_ID, it)
                    .orderByAsc(CREATE_TIME)
            )
        } ?: emptyList()
}
