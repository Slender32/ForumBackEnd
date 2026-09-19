package com.slender.forumbackend.service.app

import com.slender.forumbackend.model.data.VersionCheckData
import com.slender.forumbackend.model.request.VersionCheckRequest
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.repository.governance.WebsiteReleaseRepository
import com.slender.forumbackend.toolkit.NumericVersion
import org.springframework.stereotype.Service

@Service
class VersionCheckService(
    private val releases: WebsiteReleaseRepository
) {
    fun check(request: VersionCheckRequest): VersionCheckData {
        val current = NumericVersion.parse(request.version)
            ?: throw InvalidRequestException("版本必须为点分隔的非负整数，且不超过64字符")
        val platform = when (request.platform) {
            "desktop" -> "Windows"
            "android" -> "Android"
            else -> throw InvalidRequestException("平台必须是desktop或android")
        }

        val latest = releases.list(false)
            .asSequence()
            .filter { it.enabled && it.deletedAt == null && it.platform == platform }
            .mapNotNull { release -> NumericVersion.parse(release.version)?.let { release to it } }
            .maxByOrNull { it.second }
            ?: return VersionCheckData(hasUpdate = false, latestVersion = request.version)
        return VersionCheckData(
            hasUpdate = latest.second > current,
            latestVersion = latest.first.version,
            changelog = latest.first.releaseNotes,
            downloadUrl = latest.first.downloadUrl,
            publishedAt = latest.first.releaseDate.timestamp,
        )
    }
}
