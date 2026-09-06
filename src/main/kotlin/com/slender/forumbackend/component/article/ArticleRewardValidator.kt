package com.slender.forumbackend.component.article

import com.slender.forumbackend.exception.ArticleAlreadyRewardedException
import com.slender.forumbackend.exception.ArticleSelfRewardException
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.repository.article.ArticleRewardRepository
import org.springframework.stereotype.Component

@Component
class ArticleRewardValidator(
    private val articleRewardRepository: ArticleRewardRepository,
) {
    fun validate(article: Article, userId: Long) {
        if (article.authorId == userId)
            throw ArticleSelfRewardException()
        if (articleRewardRepository.findReward(article.articleId, userId) != null)
            throw ArticleAlreadyRewardedException()
    }
}
