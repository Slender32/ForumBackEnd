package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.VersionCheckRequest
import com.slender.forumbackend.service.app.VersionCheckService
import org.springframework.stereotype.Service

@Service
class AppFacade(
    private val versionCheckService: VersionCheckService,
) {
    fun checkVersion(request: VersionCheckRequest) = versionCheckService.check(request)
}
