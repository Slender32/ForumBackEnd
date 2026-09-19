package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.HomeFacade
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.article.CarouselListData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/home")
@Tag(name = "Home", description = "首页API")
class HomeController(
    private val homeFacade: HomeFacade,
) {
    @GetMapping("/carousel")
    @Operation(summary = "首页轮播", description = "按运营配置顺序返回启用中的轮播。支持匿名访问。")
    @ApiResponse(responseCode = "200", description = "成功")
    @SecurityRequirements
    fun carousel(): Response<CarouselListData> = success(homeFacade.carousel())

}
