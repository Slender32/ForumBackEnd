package com.slender.forumbackend.service.article

import com.slender.forumbackend.model.entity.favorite.Favorite
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.FavoriteRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class ArticleFavoriteService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val favoriteRepository: FavoriteRepository
) {
    fun toggleFavorite(articleId: Long, userId: Long) {
        articleQueryRepository.findVisibleArticleByIdOrThrow(articleId)
        val existing = favoriteRepository.find(userId, articleId)
        if (existing != null) favoriteRepository.delete(userId, articleId)
        else favoriteRepository.insert(Favorite(userId = userId, articleId = articleId, createTime = now()))
    }
}
