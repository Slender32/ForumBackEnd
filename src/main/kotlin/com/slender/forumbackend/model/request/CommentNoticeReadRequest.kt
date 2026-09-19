package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "标记评论通知已读")
data class CommentNoticeReadRequest(
    @field:Size(max = 200, message = "一次最多标记200条通知")
    @field:Schema(description = "要标记已读的通知 ID 列表，最多 200 个")
    val noticeIds: List<Long> = emptyList(),
)
