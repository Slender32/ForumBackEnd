package com.slender.forumbackend.service.article

import com.slender.forumbackend.constant.core.Redis.Key.SEARCH_SUGGESTION
import com.slender.forumbackend.model.data.article.SearchSuggestionData
import com.slender.forumbackend.model.request.SearchSuggestionRequest
import com.slender.forumbackend.repository.article.ArticleQueryRepository
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Duration.ofMinutes

@Service
class SearchSuggestionService(
    private val articleQueryRepository: ArticleQueryRepository,
    private val tagRepository: TagRepository,
    private val redisTemplate: StringRedisTemplate,
    private val json: Json,
) {
    fun suggestions(request: SearchSuggestionRequest): SearchSuggestionData {
        val keyword = request.keyword.trim()
        val cacheKey = SEARCH_SUGGESTION + "${keyword.lowercase()}:${request.size}"
        redisTemplate.opsForValue().get(cacheKey)?.let { cached ->
            return json.parse(cached, SearchSuggestionData::class)
        }

        val suggestions = LinkedHashSet<String>()
        if (keyword.isEmpty()) {
            tagRepository.findPopular(request.size).forEach { suggestions.add(it.name) }
            if (suggestions.size < request.size) {
                articleQueryRepository.findPopularTitles(request.size).forEach { suggestions.add(it) }
            }
        } else {
            tagRepository.findByNamePrefix(keyword, request.size).forEach { suggestions.add(it.name) }
            if (suggestions.size < request.size) {
                articleQueryRepository.findTitleSuggestions(keyword, request.size).forEach { suggestions.add(it) }
            }
        }

        val data = SearchSuggestionData(suggestions.take(request.size))
        redisTemplate.opsForValue().set(cacheKey, json.format(data), SUGGESTION_CACHE_TTL)
        return data
    }

    private companion object {
        val SUGGESTION_CACHE_TTL: Duration = ofMinutes(5)
    }
}
