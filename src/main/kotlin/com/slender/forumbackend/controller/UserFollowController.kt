package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.UserFollowFacade
import com.slender.forumbackend.model.data.PageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.user.UserProfileData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "UserFollow", description = "用户关注与粉丝列表")
@RestController
@RequestMapping("/users/{uid}")
class UserFollowController(
    private val facade: UserFollowFacade,
) {
    @GetMapping("/following")
    @Operation(summary = "获取用户关注列表", description = "按页码查询指定用户关注的人，页码从 1 开始。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun following(
        @Parameter(description = "用户 ID")
        @PathVariable
        uid: Long,

        @Parameter(description = "当前页码，从 1 开始")
        @RequestParam
        @Min(1)
        page: Int = 1,

        @Parameter(description = "每页数量")
        @RequestParam
        @Min(1)
        @Max(100)
        size: Int = 20,
    ): Response<PageData<UserProfileData>> = success(facade.following(uid, page, size))

    @GetMapping("/followers")
    @Operation(summary = "获取用户粉丝列表", description = "按页码查询指定用户的粉丝，页码从 1 开始。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun followers(
        @Parameter(description = "用户 ID")
        @PathVariable
        uid: Long,

        @Parameter(description = "当前页码，从 1 开始")
        @RequestParam
        @Min(1)
        page: Int = 1,

        @Parameter(description = "每页数量")
        @RequestParam
        @Min(1)
        @Max(100)
        size: Int = 20,
    ): Response<PageData<UserProfileData>> = success(facade.followers(uid, page, size))
}
