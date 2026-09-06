package com.slender.forumbackend.facade

import com.slender.forumbackend.service.HomeService
import org.springframework.stereotype.Service

@Service
class HomeFacade(
    private val homeService: HomeService,
) {
    fun carousel() = homeService.carousel()
}
