package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminCarouselFacade
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.carousel.Carousel
import com.slender.forumbackend.model.request.AdminCarouselRequest
import com.slender.forumbackend.model.request.AdminPageRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "AdminCarousel", description = "管理端首页轮播管理")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/carousels")
@PreAuthorize("hasAuthority('carousel:manage')")
class AdminCarouselController(
    private val facade: AdminCarouselFacade,
) {
    @GetMapping
    @Operation(summary = "查询首页轮播", description = "需要登录并具有 carousel:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @ParameterObject
        @Validated request: AdminPageRequest
    ): Response<AdminPageData<Carousel>> =
        success(facade.list(request.page, request.size, request.includeDeleted))

    @PostMapping
    @Operation(summary = "新增首页轮播", description = "需要登录并具有 carousel:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun add(
        @Validated @RequestBody request: AdminCarouselRequest
    ): Response<Carousel> =
        success(facade.add(request))

    @PutMapping("/{id}")
    @Operation(summary = "修改首页轮播", description = "需要登录并具有 carousel:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun update(
        @Parameter(description = "轮播 ID")
        @PathVariable id: Long,
        @Validated @RequestBody request: AdminCarouselRequest,
    ): Response<Carousel> = success(facade.update(id, request))

    @DeleteMapping("/{id}")
    @Operation(summary = "删除首页轮播", description = "需要登录并具有 carousel:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "轮播 ID")
        @PathVariable id: Long
    ): Response<Unit> {
        facade.delete(id)
        return success()
    }
}
