package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.mapper.ArticleTagMapper
import com.slender.forumbackend.model.entity.article.relation.ArticleTag
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArticleTagRepository(
    private val articleMapper: ArticleMapper,
    private val articleTagMapper: ArticleTagMapper,
) : ServiceImpl<ArticleTagMapper, ArticleTag>(), IService<ArticleTag> {
    fun bindTag(articleId: Long, tagId: Long, createTime: LocalDateTime) {
        articleTagMapper.upsertTag(articleId, tagId, createTime)
    }

    fun findTagsByArticleIds(articleIds: Collection<Long>): List<ArticleTag> =
        articleIds
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { articleTagMapper.selectByArticleIds(it) } ?: emptyList()

    fun countVisibleByTagId(tagId: Long): Int = articleMapper.countVisibleByTagId(tagId).toInt()

    fun markDeletedByArticle(articleId: Long, now: java.time.LocalDateTime) =
        articleTagMapper.update(
            null,
            UpdateWrapper<ArticleTag>()
                .eq("article_id", articleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )

    fun markDeletedByTag(tagId: Long, now: LocalDateTime) =
        articleTagMapper.update(
            null,
                UpdateWrapper<ArticleTag>()
                .eq("tag_id", tagId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )

    fun markDeleted(articleId: Long, tagId: Long, now: LocalDateTime) =
        articleTagMapper.update(
            null,
            UpdateWrapper<ArticleTag>()
                .eq("article_id", articleId)
                .eq("tag_id", tagId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
}
