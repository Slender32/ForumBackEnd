package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.notice.CommentNoticeListData
import com.slender.forumbackend.model.request.CommentNoticeListRequest
import com.slender.forumbackend.model.request.CommentNoticeReadRequest
import com.slender.forumbackend.facade.NoticeFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notice")
@Tag(name = "Notice", description = "通知API")
class NoticeController(
    private val noticeFacade: NoticeFacade,
) {
    @GetMapping("/comment")
    @Operation(summary = "评论通知列表", description = "别人评论我的文章或回复我的评论。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun listCommentNotices(
        @ParameterObject
        @ModelAttribute
        @Validated
        request: CommentNoticeListRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<CommentNoticeListData> {
        return success(noticeFacade.listCommentNotices(userCache.uid, request))
    }

    @PostMapping("/comment/read")
    @Operation(summary = "标记评论通知已读", description = "noticeIds 为空表示全部已读。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1008 noticeIds 超限"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun markCommentNoticesRead(
        @RequestBody
        @Validated
        request: CommentNoticeReadRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        noticeFacade.markCommentNoticesRead(userCache.uid, request)
        return success()
    }
}
