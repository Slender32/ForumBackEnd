package com.slender.forumbackend.facade

import com.slender.forumbackend.service.WebsiteService
import org.springframework.stereotype.Service

@Service
class WebsiteFacade(
    private val websiteService: WebsiteService,
) {
    fun introduction() = websiteService.introduction()

    fun releases() = websiteService.releases()
}
