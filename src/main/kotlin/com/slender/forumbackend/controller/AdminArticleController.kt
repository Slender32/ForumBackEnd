package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AdminArticleFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.AdminArticleDetailData
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.model.request.AdminArticleListRequest
import com.slender.forumbackend.model.request.ArticleUpdateRequest
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "AdminArticle", description = "管理端文章管理")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "未登录或访问令牌已过期")
@ApiResponse(responseCode = "403", description = "当前用户缺少所需管理权限")
@RestController
@RequestMapping("/admin/articles")
@PreAuthorize("hasAuthority('article:manage')")
class AdminArticleController(
    private val facade: AdminArticleFacade,
) {
    @GetMapping
    @Operation(summary = "查询文章", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: AdminArticleListRequest,
    ): Response<AdminPageData<Article>> =
        success(facade.list(request.page, request.size, request.includeDeleted, request.authorId, request.status))

    @GetMapping("/{id}")
    @Operation(summary = "查询详情：文章", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun get(
        @Parameter(description = "文章 ID")
        @PathVariable id: Long
    ): Response<AdminArticleDetailData> = success(facade.detail(id))

    @PutMapping("/{id}")
    @Operation(summary = "修改文章", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun update(
        @Parameter(description = "文章 ID")
        @PathVariable
        id: Long,

        @Validated
        @RequestBody
        request: ArticleUpdateRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        operator: UserCache,
    ): Response<Article> =
        success(facade.update(id, operator.uid, operator.authorities, request))

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章", description = "需要登录并具有 article:manage 权限。")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun delete(
        @Parameter(description = "文章 ID")
        @PathVariable
        id: Long,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        operator: UserCache,
    ): Response<Unit> {
        facade.delete(id, operator.uid, operator.authorities)
        return success()
    }
}
