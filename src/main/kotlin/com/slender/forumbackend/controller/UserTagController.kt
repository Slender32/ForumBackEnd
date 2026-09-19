package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.UserTagFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.article.ArticleTagData
import com.slender.forumbackend.model.request.UserTagRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "UserTag", description = "用户标签管理")
@RestController
@RequestMapping("/users/{uid}/tags")
class UserTagController(
    private val facade: UserTagFacade,
) {
    @GetMapping
    @Operation(summary = "获取用户标签", description = "返回指定用户关联的标签列表。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @Parameter(description = "用户 ID")
        @PathVariable uid: Long
    ): Response<List<ArticleTagData>> = success(facade.list(uid))

    @PostMapping
    @Operation(summary = "添加用户标签", description = "需要登录；本人或具有 relation:manage 权限的用户可操作。")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun add(
        @Parameter(description = "用户 ID")
        @PathVariable
        uid: Long,

        @Validated
        @RequestBody
        request: UserTagRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        user: UserCache,
    ): Response<Unit> {
        facade.add(uid, request, user.uid, user.authorities)
        return success()
    }

    @DeleteMapping("/{tagId}")
    @Operation(summary = "移除用户标签", description = "需要登录；本人或具有 relation:manage 权限的用户可操作。")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "用户 ID")
        @PathVariable
        uid: Long,

        @Parameter(description = "标签 ID")
        @PathVariable
        tagId: Long,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        user: UserCache,
    ): Response<Unit> {
        facade.delete(uid, tagId, user.uid, user.authorities)
        return success()
    }
}
