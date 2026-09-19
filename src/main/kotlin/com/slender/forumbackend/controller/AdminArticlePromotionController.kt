package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminArticlePromotionFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.article.content.ArticlePromotion
import com.slender.forumbackend.model.request.AdminArticlePromotionListRequest
import com.slender.forumbackend.model.request.ArticlePromotionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "AdminArticlePromotion", description = "管理端文章推荐管理")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/article-promotions")
@PreAuthorize("hasAuthority('article:manage')")
class AdminArticlePromotionController(
    private val facade: AdminArticlePromotionFacade,
) {
    @GetMapping
    @Operation(summary = "查询文章推荐", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminArticlePromotionListRequest,
    ): Response<AdminPageData<ArticlePromotion>> =
        success(facade.list(request.page, request.size, request.includeDeleted, request.articleId))

    @GetMapping("/{id}")
    @Operation(summary = "查询详情：文章推荐", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun get(
        @Parameter(description = "文章推荐 ID")
        @PathVariable id: Long,
    ): Response<ArticlePromotion> = success(facade.get(id))

    @PostMapping("/{articleId}")
    @Operation(summary = "新增文章推荐", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun add(
        @Parameter(description = "文章 ID")
        @PathVariable
        articleId: Long,

        @Validated
        @RequestBody 
        request: ArticlePromotionRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal operator: UserCache,
    ): Response<ArticlePromotion> =
        success(facade.add(articleId, operator.uid, request))

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章推荐", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "文章推荐 ID")
        @PathVariable id: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal operator: UserCache,
    ): Response<Unit> {
        facade.delete(id, operator.authorities)
        return success()
    }
}
