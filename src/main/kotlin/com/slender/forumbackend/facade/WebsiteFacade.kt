package com.slender.forumbackend.facade

import com.slender.forumbackend.service.WebsiteQueryService
import org.springframework.stereotype.Service

@Service
class WebsiteFacade(
    private val websiteService: WebsiteQueryService,
) {
    fun introduction() = websiteService.introduction()

    fun releases() = websiteService.releases()
}
