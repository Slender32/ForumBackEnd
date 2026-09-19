package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "顶级评论游标分页查询条件")
data class CommentListRequest(
    @field:Min(-1)
    @field:Schema(description = "上一页游标评论 ID，首次请求传 -1")
    val cursorCommentId: Long = -1L,
    @field:Min(0)
    @field:Schema(description = "上一页游标发布时间戳，单位毫秒；首次请求不传")
    val cursorPublishTime: Long? = null,
    @field:Min(1) @field:Max(20)
    @field:Schema(description = "每页数量，范围 1..20，默认 20")
    val size: Int = 20,
    @field:Schema(description = "排序方式；Hot 返回热门评论且不使用游标分页")
    val sort: CommentSort = CommentSort.Latest,
)

@Schema(description = "评论排序方式：Latest、Newest 为最新优先，Oldest 为最早优先，Hot 为热门评论")
enum class CommentSort {
    Latest, Hot, Newest, Oldest
}
