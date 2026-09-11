package com.slender.forumbackend.service.admin

import com.slender.forumbackend.component.common.AuditLogger
import com.slender.forumbackend.exception.AdminResourceNotFoundException
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.entity.governance.WebsiteIntroduction
import com.slender.forumbackend.model.entity.governance.WebsiteRelease
import com.slender.forumbackend.model.request.WebsiteIntroductionRequest
import com.slender.forumbackend.model.request.WebsiteReleaseRequest
import com.slender.forumbackend.repository.governance.WebsiteIntroductionRepository
import com.slender.forumbackend.repository.governance.WebsiteReleaseRepository
import java.time.LocalDateTime
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class AdminWebsiteService(
    private val introductionRepository: WebsiteIntroductionRepository,
    private val releaseRepository: WebsiteReleaseRepository,
    private val audit: AuditLogger,
    private val redis: StringRedisTemplate,
) {
    private fun invalidate() {
        redis.delete(listOf("WebsiteIntroduction", "WebsiteReleases"))
    }

    fun introductions(
        page: Int = 1,
        size: Int = 20,
        includeDeleted: Boolean = false,
    ): AdminPageData<WebsiteIntroduction> {
        val all = introductionRepository.list(includeDeleted)
        val s = size.coerceIn(1, 100)
        val p = page.coerceAtLeast(1)
        return AdminPageData(all.drop((p - 1) * s).take(s), p, s, all.size.toLong())
    }

    fun releases(
        page: Int = 1,
        size: Int = 20,
        includeDeleted: Boolean = false,
    ): AdminPageData<WebsiteRelease> {
        val all = releaseRepository.list(includeDeleted)
        val s = size.coerceIn(1, 100)
        val p = page.coerceAtLeast(1)
        return AdminPageData(all.drop((p - 1) * s).take(s), p, s, all.size.toLong())
    }

    fun introduction(id: Long) =
        introductionRepository.find(id) ?: throw AdminResourceNotFoundException("Website 介绍不存在")

    fun release(id: Long) =
        releaseRepository.find(id) ?: throw AdminResourceNotFoundException("Website 版本不存在")

    fun addIntro(req: WebsiteIntroductionRequest, op: Long) =
        introductionRepository.insert(
            WebsiteIntroduction(0, req.title.trim(),
                req.description.trim(),
                req.imageUrl.trim(), req.sortOrder, req.enabled,
                null)
        ).also {
            invalidate()
            audit.log(op, "website:manage", "website_introduction", null, "CREATE")
        }

    fun updateIntro(id: Long, req: WebsiteIntroductionRequest, op: Long): Int {
        val current = introductionRepository.find(id) ?: throw AdminResourceNotFoundException("Website 介绍不存在")
        val result = introductionRepository.update(
            current.copy(
                title = req.title.trim(),
                description = req.description.trim(),
                imageUrl = req.imageUrl.trim(),
                sortOrder = req.sortOrder,
                enabled = req.enabled,
                updateTime = LocalDateTime.now()
            )
        )
        invalidate()
        audit.log(op, "website:manage", "website_introduction", id.toString(), "UPDATE")
        return result
    }

    fun deleteIntro(id: Long, op: Long) =
        introductionRepository.markDeleted(id, LocalDateTime.now())
            .also {
                invalidate()
                audit.log(op, "website:manage", "website_introduction", id.toString(), "DELETE")
            }

    fun addRelease(req: WebsiteReleaseRequest, op: Long) =
        releaseRepository.insert(WebsiteRelease(0, req.platform, req.version, req.title, req.releaseNotes, req.sha256.lowercase(), req.downloadUrl, req.releaseDate ?: LocalDateTime.now(), req.enabled, null))
            .also {
                invalidate()
                audit.log(op, "website:manage", "website_release", null, "CREATE")
            }

    fun updateRelease(id: Long, req: WebsiteReleaseRequest, op: Long): Int {
        val current = releaseRepository.find(id) ?: throw AdminResourceNotFoundException("Website 版本不存在")
        val result = releaseRepository.update(
            current.copy(
                platform = req.platform,
                version = req.version,
                title = req.title,
                releaseNotes = req.releaseNotes,
                sha256 = req.sha256.lowercase(),
                downloadUrl = req.downloadUrl,
                releaseDate = req.releaseDate ?: current.releaseDate,
                enabled = req.enabled,
                updateTime = LocalDateTime.now())
        )
        invalidate()
        audit.log(op, "website:manage", "website_release", id.toString(), "UPDATE")
        return result
    }

    fun deleteRelease(id: Long, op: Long) =
        releaseRepository.markDeleted(id, LocalDateTime.now())
            .also {
                invalidate()
                audit.log(op, "website:manage", "website_release", id.toString(), "DELETE")
            }
}
