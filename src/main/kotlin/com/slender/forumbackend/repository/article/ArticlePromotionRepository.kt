package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticlePromotionMapper
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArticlePromotionRepository(
    private val articlePromotionMapper: ArticlePromotionMapper
) : ServiceImpl<ArticlePromotionMapper, ArticlePromotion>(), IService<ArticlePromotion> {

    fun listAdmin(
        page: Int,
        size: Int,
        includeDeleted: Boolean = false,
        articleId: Long? = null,
    ): List<ArticlePromotion> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        return articlePromotionMapper.selectList(
            QueryWrapper<ArticlePromotion>()
                .apply(if (includeDeleted) "1=1" else "deleted_at IS NULL")
                .eq(articleId != null, "article_id", articleId)
                .orderByDesc("promote_time")
                .orderByDesc("promotion_id")
                .last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        )
    }

    fun countAdmin(includeDeleted: Boolean = false, articleId: Long? = null): Long =
        articlePromotionMapper.selectCount(
            QueryWrapper<ArticlePromotion>()
                .apply(if (includeDeleted) "1=1" else "deleted_at IS NULL")
                .eq(articleId != null, "article_id", articleId)
        )

    fun findById(id: Long): ArticlePromotion? = articlePromotionMapper.selectById(id)

    fun findPromotionsByArticleIds(articleIds: Collection<Long>) =
        articleIds
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { articlePromotionMapper.selectByArticleIds(it) } ?: emptyList()

    fun markDeletedByArticle(articleId: Long, now: LocalDateTime) =
        articlePromotionMapper.update(
            null,
                UpdateWrapper<ArticlePromotion>()
                .eq("article_id", articleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )

    fun markDeleted(id: Long, now: LocalDateTime) =
        articlePromotionMapper.update(
            null,
                UpdateWrapper<ArticlePromotion>()
                .eq("promotion_id", id)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
}
