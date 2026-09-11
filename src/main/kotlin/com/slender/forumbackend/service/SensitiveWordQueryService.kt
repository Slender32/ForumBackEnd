package com.slender.forumbackend.service

import com.slender.forumbackend.component.common.ModerationResult
import com.slender.forumbackend.component.common.ModerationStatus
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.governance.SensitiveWordData
import com.slender.forumbackend.model.entity.governance.SensitiveWord
import com.slender.forumbackend.repository.governance.SensitiveWordRepository
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class SensitiveWordQueryService(
    private val repository: SensitiveWordRepository,
    private val redis: StringRedisTemplate,
    private val json: Json,
) {
    fun evaluate(input: String): ModerationResult {
        val hits = words().filter { word ->
            if (word.matchType == "EXACT") input.equals(word.word, ignoreCase = true)
            else input.contains(word.word, ignoreCase = true)
        }
        if (hits.any { it.action == "BLOCK" }) return ModerationResult(ModerationStatus.BLOCKED, input)
        var text = input
        hits.filter { it.action == "REPLACE" }.forEach { word ->
            text = text.replace(word.word, word.replacement ?: "***", ignoreCase = true)
        }
        return when {
            hits.any { it.action == "REVIEW" } -> ModerationResult(ModerationStatus.REVIEW_REQUIRED, text)
            text != input -> ModerationResult(ModerationStatus.REPLACED, text)
            else -> ModerationResult(ModerationStatus.ALLOW, text)
        }
    }

    fun words(includeDeleted: Boolean = false): List<SensitiveWord> {
        if (!includeDeleted) redis.opsForValue().get(CACHE_KEY)?.let { cached ->
            runCatching { json.parse(cached, Array<SensitiveWord>::class).toList() }.getOrNull()?.let { return it }
        }
        val result = repository.list(false).filter { it.enabled }
        if (!includeDeleted) redis.opsForValue().set(CACHE_KEY, json.format(result), Duration.ofMinutes(10))
        return if (includeDeleted) repository.list(true) else result
    }

    fun adminWords(page: Int, size: Int, includeDeleted: Boolean, keyword: String?, enabled: Boolean?): AdminPageData<SensitiveWordData> {
        val safePage = page.coerceAtLeast(1)
        val safeSize = size.coerceIn(1, 100)
        val trimmed = keyword?.trim()?.takeIf { it.isNotEmpty() }
        val items = repository.list(includeDeleted, trimmed, enabled, safePage, safeSize)
        return AdminPageData(items.map { it.toData() }, safePage, safeSize, repository.count(includeDeleted, trimmed, enabled))
    }

    companion object {
        const val CACHE_KEY = "SensitiveWords"
    }
}
