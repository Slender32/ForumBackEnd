package com.slender.forumbackend.service.app

import com.slender.forumbackend.model.data.VersionCheckData
import com.slender.forumbackend.model.request.VersionCheckRequest
import org.springframework.stereotype.Service

@Service
class VersionCheckService {
    fun check(request: VersionCheckRequest): VersionCheckData {
        //TODO 接入版本发布配置或版本管理表。
        return VersionCheckData(
            hasUpdate = false,
            latestVersion = request.version,
            latestBuildNumber = request.buildNumber,
        )
    }
}
