package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CommentReplyListRequest(
    val cursorReplyId: Long = -1L,
    val cursorPublishTime: Long? = null,
    @field:Min(1) @field:Max(50)
    val size: Int = 10,
)
