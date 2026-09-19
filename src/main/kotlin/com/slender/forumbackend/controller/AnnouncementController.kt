package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.AnnouncementFacade
import com.slender.forumbackend.model.cache.UserCache
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.announcement.AnnouncementListData
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@RestController
class AnnouncementController(
    private val facade: AnnouncementFacade
) {
    @GetMapping("/announcements")
    @Operation(summary = "可见公告", description = "匿名 isRead/unreadCount 为 null；登录时返回个人状态。未读数覆盖 placement 下全部可见公告，不受 limit 限制；limit 为 1..100。")
    fun list(
        @RequestParam
        placement: String,

        @RequestParam(defaultValue = "5")
        limit: Int,
        @AuthenticationPrincipal
        userCache: UserCache?,
    ): Response<AnnouncementListData> =
        success(facade.list(placement, limit, userCache?.uid))

    @PostMapping("/announcements/{id}/read")
    @Operation(summary = "标记已读", description = "重复调用幂等，保留首次阅读时间；未发布、禁用或不存在的公告返回 404。")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "404", description = "1901 ANNOUNCEMENT_NOT_FOUND")
    fun read(
        @PathVariable
        id: Long,

        @AuthenticationPrincipal
        userCache: UserCache,
    ): Response<Unit> {
        facade.read(id, userCache.uid)
        return success()
    }
}
