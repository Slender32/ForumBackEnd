package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.entity.article.content.ArticleStatistic
import com.slender.forumbackend.model.entity.article.relation.ArticleLike
import com.slender.forumbackend.model.entity.article.relation.ArticleReaction
import com.slender.forumbackend.model.entity.article.relation.ArticleReward
import com.slender.forumbackend.model.entity.article.relation.ArticleTag
import java.time.LocalDateTime
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ArticleMapper : BaseMapper<Article> {
    fun selectVisibleById(@Param("articleId") articleId: Long): Article?

    fun selectVisiblePage(
        @Param("cursorPublishTime") cursorPublishTime: LocalDateTime?,
        @Param("cursorArticleId") cursorArticleId: Long,
        @Param("limit") limit: Int,
        @Param("tagId") tagId: Long?,
        @Param("authorId") authorId: Long?,
        @Param("articleIds") articleIds: List<Long>?,
    ): List<Article>

    fun selectVisiblePageByKeyword(
        @Param("cursorPublishTime") cursorPublishTime: LocalDateTime?,
        @Param("cursorArticleId") cursorArticleId: Long,
        @Param("limit") limit: Int,
        @Param("keywordPattern") keywordPattern: String,
    ): List<Article>

    fun selectVisibleByIds(
        @Param("articleIds") articleIds: List<Long>
    ): List<Article>

    fun selectTitleSuggestions(
        @Param("pattern") pattern: String,
        @Param("limit") limit: Int,
    ): List<Article>

    fun selectPopularTitles(
        @Param("limit") limit: Int
    ): List<Article>

    fun countVisibleByTagId(
        @Param("tagId") tagId: Long
    ): Long
}

@Mapper
interface ArticleContentMapper : BaseMapper<ArticleContent>

@Mapper
interface ArticleStatisticMapper : BaseMapper<ArticleStatistic>

@Mapper
interface ArticlePromotionMapper : BaseMapper<ArticlePromotion> {
    fun selectByArticleIds(
        @Param("articleIds") articleIds: List<Long>
    ): List<ArticlePromotion>
}

@Mapper
interface ArticleLikeMapper : BaseMapper<ArticleLike> {
    fun selectByArticleAndUser(
        @Param("articleId") articleId: Long,
        @Param("userId") userId: Long,
    ): ArticleLike?

    fun selectArticleIdsByUser(
        @Param("userId") userId: Long,
        @Param("articleIds") articleIds: List<Long>,
    ): List<Long>

    fun deleteByArticleAndUser(
        @Param("articleId") articleId: Long,
        @Param("userId") userId: Long,
    ): Int

    fun upsertLike(
        @Param("articleId") articleId: Long,
        @Param("userId") userId: Long,
        @Param("createTime") createTime: LocalDateTime,
    ): Int
}

@Mapper
interface ArticleReactionMapper : BaseMapper<ArticleReaction> {
    fun selectByArticleIds(
        @Param("articleIds") articleIds: List<Long>
    ): List<ArticleReaction>

    fun upsertReaction(
        @Param("articleId") articleId: Long,
        @Param("userId") userId: Long,
        @Param("emoji") emoji: String,
        @Param("createTime") createTime: LocalDateTime,
    )
}

@Mapper
interface ArticleTagMapper : BaseMapper<ArticleTag> {
    fun selectByArticleIds(
        @Param("articleIds") articleIds: List<Long>
    ): List<ArticleTag>

    fun upsertTag(
        @Param("articleId") articleId: Long,
        @Param("tagId") tagId: Long,
        @Param("createTime") createTime: LocalDateTime,
    ): Int
}

@Mapper
interface ArticleRewardMapper : BaseMapper<ArticleReward> {
    fun selectByArticleAndUser(
        @Param("articleId") articleId: Long,
        @Param("userId") userId: Long,
    ): ArticleReward?
}
