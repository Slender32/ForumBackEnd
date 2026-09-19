package com.slender.forumbackend.model.data.comment

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "顶级评论分页结果")
data class CommentListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<CommentData>,
    @field:Schema(description = "下一页游标；没有后续数据时为 null")
    val nextCursor: CommentCursorData?,
    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
    @field:Schema(description = "评论总数")
    val totalCount: Int,
)

@Schema(description = "评论分页游标")
data class CommentCursorData(
    @field:Schema(description = "评论 ID")
    val commentId: Long,
    @field:Schema(description = "发布时间，Unix 毫秒时间戳")
    val publishTime: Long,
)
