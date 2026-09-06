package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.WebsiteFacade
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.website.WebsiteIntroductionData
import com.slender.forumbackend.model.data.website.WebsiteReleaseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/website")
@Tag(name = "Website", description = "官网公开API")
class WebsiteController(
    private val websiteFacade: WebsiteFacade,
) {
    @GetMapping("/introduction")
    @Operation(summary = "官网介绍内容", description = "返回官网介绍页的文字和轮播图片。支持匿名访问。")
    @ApiResponse(responseCode = "200", description = "成功")
    fun introduction(): Response<WebsiteIntroductionData> = success(websiteFacade.introduction())

    @GetMapping("/releases")
    @Operation(summary = "官网下载版本", description = "返回 Windows 和 Android 客户端的公开下载信息。支持匿名访问。")
    @ApiResponse(responseCode = "200", description = "成功")
    fun releases(): Response<WebsiteReleaseData> = success(websiteFacade.releases())
}
