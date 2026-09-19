package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AnnouncementFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.announcement.AnnouncementListData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Announcement", description = "公告查询与阅读状态")
@RestController
class AnnouncementController(
    private val facade: AnnouncementFacade
) {
    @GetMapping("/announcements")
    @Operation(summary = "可见公告", description = "匿名 isRead/unreadCount 为 null；登录时返回个人状态。未读数覆盖 placement 下全部可见公告，不受 limit 限制；limit 为 1..100。")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun list(
        @Parameter(description = "公告展示位置标识，不能为空，最多 64 字符")
        @RequestParam
        placement: String,

        @Parameter(description = "返回条数，范围 1..100，默认 5")
        @RequestParam(defaultValue = "5")
        limit: Int,
        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<AnnouncementListData> =
        success(facade.list(placement, limit, userCache?.uid))

    @PostMapping("/announcements/{id}/read")
    @Operation(summary = "标记已读", description = "重复调用幂等，保留首次阅读时间；未发布、禁用或不存在的公告返回 404。")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "404", description = "1901 ANNOUNCEMENT_NOT_FOUND")
    @ApiResponse(responseCode = "200", description = "操作成功，code 为 0；data 结构见响应模型", useReturnTypeSchema = true)
    fun read(
        @Parameter(description = "公告 ID")
        @PathVariable
        id: Long,

        @Parameter(hidden = true)
        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        facade.read(id, userCache.uid)
        return success()
    }
}
