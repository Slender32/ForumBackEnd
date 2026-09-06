package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AppFacade
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.VersionCheckData
import com.slender.forumbackend.model.request.VersionCheckRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/app")
@Tag(name = "App", description = "客户端设置和版本API")
class AppController(
    private val appFacade: AppFacade,
) {
    @GetMapping("/version/check")
    @Operation(summary = "检查客户端版本", description = "当前阶段返回无更新占位结果。")
    @ApiResponse(responseCode = "200", description = "检查成功")
    @SecurityRequirement(name = "bearerAuth")
    fun checkVersion(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: VersionCheckRequest,
    ): Response<VersionCheckData> = success(appFacade.checkVersion(request))
}
