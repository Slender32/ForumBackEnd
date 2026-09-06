package com.slender.forumbackend.controller

import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.model.data.article.ArticleListData
import com.slender.forumbackend.model.data.user.FollowToggleData
import com.slender.forumbackend.model.data.user.UserCommentListData
import com.slender.forumbackend.model.data.user.UserProfileData
import com.slender.forumbackend.model.data.SessionData
import com.slender.forumbackend.model.data.user.CancelAccountData
import com.slender.forumbackend.model.data.user.UpdatePasswordData
import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.request.FavoriteListRequest
import com.slender.forumbackend.model.request.ReportRequest
import com.slender.forumbackend.model.request.UserCommentListRequest
import com.slender.forumbackend.model.request.CancelAccountRequest
import com.slender.forumbackend.model.request.RebindEmailRequest
import com.slender.forumbackend.model.request.UpdateAvatarRequest
import com.slender.forumbackend.model.request.UpdatePasswordRequest
import com.slender.forumbackend.model.request.UpdateSignatureRequest
import com.slender.forumbackend.facade.UserFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "User", description = "用户相关API")
class UserController(
    private val userFacade: UserFacade,
) {
    @PutMapping("/user/me/avatar")
    @Operation(summary = "修改头像", description = "头像地址必须来自当前站点的图片上传接口。")
    @SecurityRequirement(name = "bearerAuth")
    fun updateAvatar(
        @RequestBody @Validated request: UpdateAvatarRequest,
        @AuthenticationPrincipal userCache: UserCache,
    ): Response<UserData> = success(userFacade.updateAvatar(userCache.uid, request))

    @PutMapping("/user/me/signature")
    @Operation(summary = "修改签名", description = "允许传空字符串清空签名。")
    @SecurityRequirement(name = "bearerAuth")
    fun updateSignature(
        @RequestBody @Validated request: UpdateSignatureRequest,
        @AuthenticationPrincipal userCache: UserCache,
    ): Response<UserData> = success(userFacade.updateSignature(userCache.uid, request))

    @PutMapping("/user/me/email")
    @Operation(summary = "完成邮箱改绑", description = "验证码先通过 POST /auth/captcha 获取，再在此接口提交。")
    @SecurityRequirement(name = "bearerAuth")
    fun rebindEmail(
        @RequestBody @Validated request: RebindEmailRequest,
        @AuthenticationPrincipal userCache: UserCache,
    ): Response<SessionData> = success(userFacade.rebindEmail(userCache.uid, request))

    @PutMapping("/user/me/password")
    @Operation(summary = "修改密码", description = "修改成功后当前账号已有登录缓存失效。")
    @SecurityRequirement(name = "bearerAuth")
    fun updatePassword(
        @RequestBody @Validated request: UpdatePasswordRequest,
        @AuthenticationPrincipal userCache: UserCache,
    ): Response<UpdatePasswordData> = success(userFacade.updatePassword(userCache.uid, request))

    @DeleteMapping("/user/me")
    @Operation(summary = "注销账号", description = "需要当前密码和通过 auth/captcha 获取的当前邮箱验证码。")
    @SecurityRequirement(name = "bearerAuth")
    fun cancelAccount(
        @RequestBody @Validated request: CancelAccountRequest,
        @AuthenticationPrincipal userCache: UserCache,
    ): Response<CancelAccountData> = success(userFacade.cancelAccount(userCache.uid, request))

    @GetMapping("/users/{uid}/profile")
    @Operation(summary = "用户主页资料", description = "支持匿名访问。登录后补充 isFollowing / isSelf。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun profile(
        @PathVariable
        @Parameter(description = "用户ID")
        uid: Long,

        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<UserProfileData> {
        return success(userFacade.profile(uid, userCache?.uid))
    }

    @GetMapping("/users/{uid}/article")
    @Operation(summary = "用户发布的文章", description = "只返回已发布公开文章。支持匿名访问。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun listArticles(
        @PathVariable
        @Parameter(description = "用户ID")
        uid: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: ArticleListRequest,

        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<ArticleListData> {
        return success(userFacade.listArticles(uid, request, userCache?.uid))
    }

    @GetMapping("/users/{uid}/comment")
    @Operation(summary = "用户发表的评论", description = "支持匿名访问。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun listComments(
        @PathVariable
        @Parameter(description = "用户ID")
        uid: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: UserCommentListRequest,
    ): Response<UserCommentListData> {
        return success(userFacade.listComments(uid, request))
    }

    @GetMapping("/users/{uid}/favorite")
    @Operation(summary = "用户收藏", description = "仅本人可见。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "403", description = "1009 查看他人收藏"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun listFavorites(
        @PathVariable
        @Parameter(description = "用户ID")
        uid: Long,

        @ParameterObject
        @ModelAttribute
        @Validated
        request: FavoriteListRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<ArticleListData> {
        return success(userFacade.listFavorites(uid, request, userCache.uid))
    }

    @PostMapping("/users/{uid}/follow")
    @Operation(summary = "关注或取消关注", description = "toggle 语义。需要登录。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1008 不能关注自己"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun toggleFollow(
        @PathVariable
        @Parameter(description = "被关注用户ID")
        uid: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<FollowToggleData> {
        return success(userFacade.toggleFollow(uid, userCache.uid))
    }

    @PostMapping("/users/{uid}/report")
    @Operation(summary = "举报用户", description = "不能举报自己。同一人对同一用户只能举报一次。")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "成功"),
            ApiResponse(responseCode = "400", description = "1008/1602 参数或理由不合法"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
            ApiResponse(responseCode = "404", description = "1101 用户不存在"),
            ApiResponse(responseCode = "409", description = "1601 重复举报"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    fun reportUser(
        @PathVariable
        @Parameter(description = "被举报用户ID")
        uid: Long,

        @RequestBody
        @Validated
        request: ReportRequest,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        userFacade.reportUser(uid, userCache.uid, request)
        return success()
    }
}
