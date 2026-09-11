package com.slender.forumbackend.service

import com.slender.forumbackend.constant.enumeration.website.WebsitePlatform
import com.slender.forumbackend.model.data.website.WebsiteIntroductionData
import com.slender.forumbackend.model.data.website.WebsiteIntroductionItemData
import com.slender.forumbackend.model.data.website.WebsiteReleaseData
import com.slender.forumbackend.model.data.website.WebsiteReleaseItemData
import com.slender.forumbackend.repository.governance.WebsiteIntroductionRepository
import com.slender.forumbackend.repository.governance.WebsiteReleaseRepository
import com.slender.forumbackend.toolkit.Json
import java.time.Duration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class WebsiteQueryService(
    private val introductions: WebsiteIntroductionRepository,
    private val releasesRepository: WebsiteReleaseRepository,
    private val redis: StringRedisTemplate,
    private val json: Json,
) {
    fun introduction(): WebsiteIntroductionData {
        redis.opsForValue().get(INTRO_CACHE)?.let { cached ->
            runCatching { json.parse(cached, WebsiteIntroductionData::class) }
                .getOrNull()
                ?.let {
                    return it
                }
        }
        val rows = introductions.list(false).filter { it.enabled }.map {
            WebsiteIntroductionItemData(it.id, it.title, it.description, it.imageUrl)
        }
        val data = WebsiteIntroductionData(rows)
        redis.opsForValue().set(INTRO_CACHE, json.format(data), Duration.ofMinutes(10))
        return data
    }

    fun releases(): WebsiteReleaseData {
        redis.opsForValue().get(RELEASE_CACHE)?.let { cached ->
            runCatching { json.parse(cached, WebsiteReleaseData::class) }
                .getOrNull()
                ?.let {
                    return it
                }
        }
        val rows = releasesRepository.list(false).filter { it.enabled }.map {
            WebsiteReleaseItemData(WebsitePlatform.valueOf(it.platform), it.version, it.sha256, it.downloadUrl)
        }
        val data = WebsiteReleaseData(rows)
        redis.opsForValue().set(RELEASE_CACHE, json.format(data), Duration.ofMinutes(10))
        return data
    }

    private companion object {
        const val INTRO_CACHE = "WebsiteIntroduction"
        const val RELEASE_CACHE = "WebsiteReleases"
    }
}

@Service
class WebsiteService(private val query: WebsiteQueryService) {
    fun introduction() = query.introduction()
    fun releases() = query.releases()
}
