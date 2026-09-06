package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "标记评论通知已读")
data class CommentNoticeReadRequest(
    @field:Size(max = 200, message = "一次最多标记200条通知")
    val noticeIds: List<Long> = emptyList(),
)
