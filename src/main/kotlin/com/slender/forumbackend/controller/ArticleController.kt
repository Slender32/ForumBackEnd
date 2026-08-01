package com.slender.forumbackend.controller

import com.slender.forumbackend.model.article.ArticleContentData
import com.slender.forumbackend.model.article.ArticleListData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.service.ArticleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
@Tag(name = "文章", description = "文章列表、文章详情和文章卡片数据")
class ArticleController(
    private val articleService: ArticleService
){
    @GetMapping("/list")
    @Operation(summary = "获取文章列表", description = "使用复合游标分页获取首页文章流。首次请求传 cursorArticleId=-1 " +
            "且不传 cursorPublishTime；后续请求传上一次返回的 nextCursor.articleId 和 nextCursor.publishTime。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "获取成功"),
            ApiResponse(responseCode = "400", description = "1008 请求参数错误"),
            ApiResponse(responseCode = "404", description = "1201 文章不存在"),
        ]
    )
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        articleListRequest: ArticleListRequest,
    ): Response<ArticleListData> {
        val data = articleService.list(articleListRequest)
        return success(data)
    }

    @GetMapping("/{aid}")
    @Operation(summary = "获取文章内容", description = "根据文章ID返回 Markdown 内容。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "获取成功"),
            ApiResponse(responseCode = "404", description = "1201 文章不存在"),
        ]
    )
    fun content(
        @Parameter(description = "文章ID")
        @PathVariable
        aid: Long,
    ): Response<ArticleContentData> {
        val data = articleService.content(aid)
        return success(data)
    }
}
