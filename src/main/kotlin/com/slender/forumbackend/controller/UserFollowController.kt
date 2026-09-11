package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.PageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.user.UserProfileData
import com.slender.forumbackend.facade.UserFollowFacade
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/{uid}")
class UserFollowController(
    private val facade: UserFollowFacade,
) {
    @GetMapping("/following")
    fun following(
        @PathVariable
        uid: Long,

        @RequestParam
        @Min(1)
        page: Int = 1,

        @RequestParam
        @Min(1)
        @Max(100)
        size: Int = 20,
    ): Response<PageData<UserProfileData>> = success(facade.following(uid, page, size))

    @GetMapping("/followers")
    fun followers(
        @PathVariable
        uid: Long,

        @RequestParam
        @Min(1)
        page: Int = 1,

        @RequestParam
        @Min(1)
        @Max(100)
        size: Int = 20,
    ): Response<PageData<UserProfileData>> = success(facade.followers(uid, page, size))
}
