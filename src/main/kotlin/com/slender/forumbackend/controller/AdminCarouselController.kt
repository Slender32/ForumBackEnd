package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.carousel.Carousel
import com.slender.forumbackend.model.request.AdminCarouselRequest
import com.slender.forumbackend.model.request.AdminPageRequest
import com.slender.forumbackend.facade.AdminCarouselFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated

@RestController
@RequestMapping("/admin/carousels")
@PreAuthorize("hasAuthority('carousel:manage')")
class AdminCarouselController(
    private val facade: AdminCarouselFacade,
) {
    @GetMapping
    fun list(
        @Validated request: AdminPageRequest
    ): Response<AdminPageData<Carousel>> =
        success(facade.list(request.page, request.size, request.includeDeleted))

    @PostMapping
    fun add(
        @Validated @RequestBody request: AdminCarouselRequest
    ): Response<Carousel> =
        success(facade.add(request))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Validated @RequestBody request: AdminCarouselRequest,
    ): Response<Carousel> = success(facade.update(id, request))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): Response<Unit> {
        facade.delete(id)
        return success()
    }
}
