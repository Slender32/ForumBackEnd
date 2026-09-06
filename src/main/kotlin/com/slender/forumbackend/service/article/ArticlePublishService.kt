package com.slender.forumbackend.service.article

import com.slender.forumbackend.component.article.ArticleFactory
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticlePublishData
import com.slender.forumbackend.model.request.ArticlePublishRequest
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.article.ArticleContentRepository
import com.slender.forumbackend.repository.article.ArticleStatisticRepository
import com.slender.forumbackend.repository.article.ArticleTagRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class ArticlePublishService(
    private val articleContentRepository: ArticleContentRepository,
    private val articleStatisticRepository: ArticleStatisticRepository,
    private val articleTagRepository: ArticleTagRepository,
    private val tagRepository: TagRepository,
    private val articleFactory: ArticleFactory
) {
    fun publish(authorId: Long, request: ArticlePublishRequest): ArticlePublishData {
        require(request.tags.size <= TAG_LIMIT)
        val createTime = now()
        val articleId = articleContentRepository.createArticle(
            articleFactory.create(authorId, request, createTime)
        )

        articleContentRepository.createContent(articleId, request.content)
        articleStatisticRepository.createStatistic(articleId)

        request.tags
            .map { it.copy(name = it.name.trim()) }
            .distinctBy { it.name to it.color }
            .forEach { tagRequest ->
                val tag = tagRepository.findOrCreate(tagRequest.name, tagRequest.color, createTime)
                articleTagRepository.bindTag(articleId, tag.tid, createTime)
            }

        return ArticlePublishData(
            articleId = articleId,
            publishTime = createTime.timestamp,
        )
    }

    private companion object {
        const val TAG_LIMIT = 10
    }
}
