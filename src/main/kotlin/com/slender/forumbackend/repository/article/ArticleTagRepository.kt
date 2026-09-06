package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.mapper.ArticleTagMapper
import com.slender.forumbackend.model.entity.article.relation.ArticleTag
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ArticleTagRepository(
    private val articleMapper: ArticleMapper,
    private val articleTagMapper: ArticleTagMapper,
) : ServiceImpl<ArticleTagMapper, ArticleTag>(), IService<ArticleTag> {
    fun bindTag(articleId: Long, tagId: Long, createTime: LocalDateTime) {
        articleTagMapper.insert(ArticleTag(articleId, tagId, createTime))
    }

    fun findTagsByArticleIds(articleIds: Collection<Long>): List<ArticleTag> =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            articleTagMapper.selectByArticleIds(it)
        } ?: emptyList()

    fun countVisibleByTagId(tagId: Long): Int =
        articleMapper.countVisibleByTagId(tagId).toInt()
}
