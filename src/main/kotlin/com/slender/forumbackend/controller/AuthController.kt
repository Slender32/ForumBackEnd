package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.RefreshData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "登录、登出和Token刷新")
class AuthController(
    private val userService: UserService
) {

    @GetMapping("/refresh")
    @Operation(
        summary = "刷新Token",
        description = "使用refreshToken刷新登录状态。请求头需携带 Authorization: Bearer <refreshToken>。",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "刷新成功"),
            ApiResponse(responseCode = "400", description = "Token格式错误或登录尚未过期"),
            ApiResponse(responseCode = "401", description = "Token缺失或已过期"),
            ApiResponse(responseCode = "403", description = "用户被禁用")
        ]
    )
    fun refresh(
        @Parameter(hidden = true)
        @AuthenticationPrincipal uid: Long
    ): Response<RefreshData> {
        val data = userService.refresh(uid)
        return success(data)
    }
}
