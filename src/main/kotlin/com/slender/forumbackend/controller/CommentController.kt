package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.CommentFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.comment.CommentCreateData
import com.slender.forumbackend.model.data.comment.CommentDeleteData
import com.slender.forumbackend.model.data.comment.CommentListData
import com.slender.forumbackend.model.data.comment.CommentReplyListData
import com.slender.forumbackend.model.request.CommentCreateRequest
import com.slender.forumbackend.model.request.CommentListRequest
import com.slender.forumbackend.model.request.CommentReplyListRequest
import com.slender.forumbackend.model.request.CommentReplyRequest
import com.slender.forumbackend.model.request.ReportRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
@Tag(name = "Comment", description = "评论互动API")
class CommentController(
    private val commentFacade: CommentFacade,
) {
    @GetMapping("/article/{aid}/comment")
    @Operation(summary = "获取文章评论列表", description = "获取文章的顶级评论列表,每条评论带前3条回复。支持匿名访问，登录后补充 isLiked。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "400", description = "1008 请求参数不合法"),
                ApiResponse(responseCode = "401", description = "1011 未登录"),
                ApiResponse(responseCode = "404", description = "1201 文章不存在"),
            ]
    )
    @SecurityRequirements
    fun getCommentList(
        @PathVariable
        @Parameter(description = "文章ID")
        aid: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: CommentListRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<CommentListData> {
        val data = commentFacade.getCommentList(aid, request, userCache?.uid)
        return success(data)
    }

    @PostMapping("/article/{aid}/comment")
    @Operation(summary = "发表顶级评论", description = "在文章下发表评论。需要登录。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "400", description = "1302 评论内容不合法"),
                ApiResponse(responseCode = "401", description = "1011 未登录"),
                ApiResponse(responseCode = "404", description = "1201 文章不存在"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun createComment(
        @PathVariable
        @Parameter(description = "文章ID")
        aid: Long,
        @RequestBody
        @Validated
        request: CommentCreateRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<CommentCreateData> {
        val data = commentFacade.createComment(aid, userCache.uid, request.content)
        return success(data)
    }

    @PutMapping("/comment/{cid}")
    @Operation(summary = "修改评论", description = "修改评论正文，需要登录并通过评论操作权限校验。")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun updateComment(
        @Parameter(description = "评论 ID")
        @PathVariable
        cid: Long,

        @RequestBody
        @Validated
        request: CommentCreateRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        commentFacade.updateComment(cid, userCache.uid, userCache.authorities, request.content)
        return success()
    }

    @PostMapping("/comment/{cid}/reply")
    @Operation(summary = "回复评论", description = "回复某条评论(顶级或嵌套)。需要登录。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "400", description = "1302 评论内容不合法"),
                ApiResponse(responseCode = "401", description = "1011 未登录"),
                ApiResponse(responseCode = "404", description = "1301 评论不存在或已删除"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun replyComment(
        @PathVariable
        @Parameter(description = "被回复的评论ID")
        cid: Long,
        @RequestBody
        @Validated
        request: CommentReplyRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<CommentCreateData> {
        val data = commentFacade.replyComment(cid, userCache.uid, request.content)
        return success(data)
    }

    @GetMapping("/comment/{cid}/reply")
    @Operation(
        summary = "获取某评论下的回复",
        description = "按时间正序返回该楼下全部层级回复。{cid} 为子评论时按其 root 处理。支持匿名访问。",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "400", description = "1008 请求参数不合法"),
                ApiResponse(responseCode = "404", description = "1301 评论不存在"),
            ]
    )
    @SecurityRequirements
    fun getReplyList(
        @PathVariable
        @Parameter(description = "评论ID，顶级或子评论均可")
        cid: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: CommentReplyListRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<CommentReplyListData> {
        val data = commentFacade.getReplyList(cid, request, userCache?.uid)
        return success(data)
    }

    @PostMapping("/comment/{cid}/report")
    @Operation(summary = "举报评论", description = "同一人对同一评论只能举报一次。需要登录。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "400", description = "1008/1602 参数或理由不合法"),
                ApiResponse(responseCode = "401", description = "1011 未登录"),
                ApiResponse(responseCode = "404", description = "1301 评论不存在"),
                ApiResponse(responseCode = "409", description = "1601 重复举报"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun reportComment(
        @PathVariable
        @Parameter(description = "评论ID")
        cid: Long,

        @RequestBody
        @Validated
        request: ReportRequest,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        commentFacade.reportComment(cid, userCache.uid, request)
        return success()
    }

    @DeleteMapping("/comment/{cid}")
    @Operation(summary = "删除评论", description = "作者或拥有 comment:delete:any 的管理员可删。软删除。")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "401", description = "1011 未登录"),
                ApiResponse(responseCode = "403", description = "1304 无权删除"),
                ApiResponse(responseCode = "404", description = "1301 评论不存在或已删除"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun deleteComment(
        @PathVariable
        @Parameter(description = "评论ID")
        cid: Long,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<CommentDeleteData> {
        return success(commentFacade.deleteComment(cid, userCache.uid))
    }

    @PostMapping("/comment/{cid}/like")
    @Operation(summary = "切换评论点赞状态", description = "切换当前用户对评论的点赞状态")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "成功"),
                ApiResponse(responseCode = "401", description = "令牌缺失或已过期"),
                ApiResponse(responseCode = "404", description = "1301 评论未找到"),
            ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun toggleLike(
        @PathVariable
        @Parameter(description = "评论ID")
        cid: Long,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        commentFacade.toggleLike(cid, userCache.uid)
        return success()
    }
}
