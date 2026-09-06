package com.slender.forumbackend.component.article

import com.slender.forumbackend.constant.core.Redis.Key.ARTICLE_VIEW
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import java.time.Duration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class ArticleViewRecorder(
    private val redisTemplate: StringRedisTemplate,
    private val articleStatisticRepository: ArticleStatisticRepository,
) {
    fun record(articleId: Long, viewerKey: String) {
        val key = "$ARTICLE_VIEW$articleId:$viewerKey"
        val firstView = redisTemplate.opsForValue().setIfAbsent(key, "1", VIEW_WINDOW) == true
        if (firstView) {
            articleStatisticRepository.incrementViewCount(articleId, 1)
        }
    }

    private companion object {
        val VIEW_WINDOW: Duration = Duration.ofMinutes(30)
    }
}
