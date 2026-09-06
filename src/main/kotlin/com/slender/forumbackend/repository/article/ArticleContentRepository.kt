package com.slender.forumbackend.repository.article

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArticleContentMapper
import com.slender.forumbackend.mapper.ArticleMapper
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.entity.article.content.ArticleContent
import org.springframework.stereotype.Repository

@Repository
class ArticleContentRepository(
    private val articleMapper: ArticleMapper,
    private val articleContentMapper: ArticleContentMapper,
) : ServiceImpl<ArticleContentMapper, ArticleContent>(), IService<ArticleContent> {
    fun findContentById(articleId: Long): ArticleContent? =
        articleContentMapper.selectById(articleId)

    fun createArticle(article: Article): Long {
        articleMapper.insert(article)
        return article.articleId
    }

    fun createContent(articleId: Long, content: String) {
        articleContentMapper.insert(ArticleContent(articleId, content))
    }
}
