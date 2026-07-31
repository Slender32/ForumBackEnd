package com.slender.forumbackend.controller

import com.slender.forumbackend.constant.core.Message.User.CAPTCHA_SENT
import com.slender.forumbackend.constant.core.Message.User.REGISTER_SUCCESS
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.RefreshData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.model.request.CaptchaRequest
import com.slender.forumbackend.model.request.RegisterRequest
import com.slender.forumbackend.service.CaptchaService
import com.slender.forumbackend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "注册、验证码、登录、登出和Token刷新")
class AuthController(
    private val userService: UserService,
    private val captchaService: CaptchaService,
) {
    @PostMapping("/captcha")
    @Operation(summary = "发送注册验证码", description = "生成6位数字验证码，首位不会为0，并异步发送到目标邮箱。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "发送成功"),
            ApiResponse(responseCode = "400", description = "1008 请求参数错误"),
        ]
    )
    fun captcha(
        @RequestBody @Validated captchaRequest: CaptchaRequest,
    ): Response<Unit> {
        captchaService.sendCaptcha(captchaRequest)
        return success(CAPTCHA_SENT)
    }

    @PostMapping("/register")
    @Operation(summary = "邮箱验证码注册", description = "校验邮箱验证码后创建用户、统计信息和默认USER角色。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "注册成功"),
            ApiResponse(responseCode = "400", description = "1008 请求参数错误；1103 验证码错误或已过期"),
            ApiResponse(responseCode = "409", description = "1102 邮箱已注册"),
        ]
    )
    fun register(
        @RequestBody @Validated registerRequest: RegisterRequest,
    ): Response<Unit> {
        userService.register(registerRequest)
        return success(REGISTER_SUCCESS)
    }

    @GetMapping("/refresh")
    @Operation(
        summary = "刷新Token",
        description = "只要refreshToken有效即可随时刷新，并返回新的accessToken、refreshToken和当前用户信息。",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "刷新成功"),
            ApiResponse(responseCode = "400", description = "1006 令牌签名或格式错误"),
            ApiResponse(responseCode = "401", description = "1001 令牌缺失；1003 refreshToken已过期"),
            ApiResponse(responseCode = "403", description = "1005 用户被封禁"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    fun refresh(
        @Parameter(hidden = true)
        @AuthenticationPrincipal uid: Long,
    ): Response<RefreshData> {
        val data = userService.refresh(uid)
        return success(data)
    }

    @GetMapping("/me")
    @Operation(
        summary = "获取当前登录用户",
        description = "用于校验accessToken是否有效，并返回当前用户的最新资料和统计信息。",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "获取成功"),
            ApiResponse(responseCode = "400", description = "1006 令牌签名或格式错误"),
            ApiResponse(responseCode = "401", description = "1001 令牌缺失；1002 accessToken已过期"),
            ApiResponse(responseCode = "403", description = "1005 用户被封禁"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    fun me(
        @Parameter(hidden = true)
        @AuthenticationPrincipal userCache: UserCache,
    ): Response<UserData> {
        val data = userService.currentUser(userCache.uid)
        return success(data)
    }
}
