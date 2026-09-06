package com.slender.forumbackend.controller

import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.article.TagDetailData
import com.slender.forumbackend.model.data.article.TagListData
import com.slender.forumbackend.model.request.TagListRequest
import com.slender.forumbackend.facade.TagFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tag")
@Tag(name = "Tag", description = "标签API")
class TagController(
    private val tagFacade: TagFacade,
) {
    @GetMapping("/list")
    @Operation(summary = "标签列表", description = "keyword 为空返回热门标签。支持匿名访问。")
    @ApiResponse(responseCode = "200", description = "成功")
    @SecurityRequirement(name = "bearerAuth")
    fun list(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: TagListRequest,
    ): Response<TagListData> {
        return success(tagFacade.list(request))
    }

    @GetMapping("/{tid}")
    @Operation(summary = "标签详情", description = "支持匿名访问。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1206 标签不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun detail(
        @PathVariable
        @Parameter(description = "标签ID")
        tid: Long,
    ): Response<TagDetailData> {
        return success(tagFacade.detail(tid))
    }
}
