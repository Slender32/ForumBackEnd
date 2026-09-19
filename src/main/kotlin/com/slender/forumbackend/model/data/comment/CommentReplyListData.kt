package com.slender.forumbackend.model.data.comment

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "评论回复分页结果")
data class CommentReplyListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<CommentData>,
    @field:Schema(description = "下一页游标；没有后续数据时为 null")
    val nextCursor: CommentCursorData?,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
    @field:Schema(description = "该顶级评论下的回复总数")
    val totalCount: Int,
)
