package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CommentListRequest(
    @field:Min(-1)
    val cursorCommentId: Long = -1L,
    @field:Min(0)
    val cursorPublishTime: Long? = null,
    @field:Min(1) @field:Max(20)
    val size: Int = 20,
    val sort: CommentSort = CommentSort.Latest,
)

enum class CommentSort {
    Latest, Hot, Newest, Oldest
}
