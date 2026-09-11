package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticleContentMapper
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArticleContentRepository(
    private val articleMapper: ArticleMapper,
    private val articleContentMapper: ArticleContentMapper,
) : ServiceImpl<ArticleContentMapper, ArticleContent>(), IService<ArticleContent> {
    fun findContentById(articleId: Long): ArticleContent? =
        articleContentMapper.selectOne(
            QueryWrapper<ArticleContent>().eq("article_id", articleId).isNull("deleted_at")
        )

    fun createArticle(article: Article): Long {
        articleMapper.insert(article)
        return article.articleId
    }

    fun createContent(articleId: Long, content: String) {
        articleContentMapper.insert(ArticleContent(articleId, content))
    }

    fun updateContent(articleId: Long, content: String): Boolean {
        val current = articleContentMapper.selectById(articleId) ?: return false
        return articleContentMapper.updateById(current.copy(content = content, deletedAt = null)) > 0
    }

    fun markDeleted(articleId: Long, now: LocalDateTime) =
        articleContentMapper.update(
            null,
            UpdateWrapper<ArticleContent>()
                .eq("article_id", articleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        ) > 0
}
