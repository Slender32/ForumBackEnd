package com.slender.forumbackend.model.request

import com.slender.forumbackend.validation.ArticleListCursor
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "文章列表游标分页请求")
@ArticleListCursor
data class ArticleListRequest(
    @field:Schema(description = "游标文章ID，首次请求传-1", example = "-1", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:Min(value = -1, message = "游标文章ID不能小于-1")
    val cursorArticleId: Long,

    @field:Schema(description = "游标文章发布时间戳，首次请求不传；非首次请求必须传", example = "1767225600000", nullable = true)
    @field:Min(value = 0, message = "游标文章发布时间不能小于0")
    val cursorPublishTime: Long? = null,

    @field:Schema(description = "每页数量，范围1到10", example = "10", minimum = "1", maximum = "10")
    @field:Min(value = 1, message = "每页数量不能小于1")
    @field:Max(value = 10, message = "每页数量不能大于10")
    val size: Int = 10,

    @field:Schema(description = "按标签筛选，不传表示不筛选", nullable = true)
    @field:Min(value = 1, message = "标签ID不合法")
    val tagId: Long? = null,
) {
    companion object {
        const val FIRST_CURSOR = -1L
    }
}
