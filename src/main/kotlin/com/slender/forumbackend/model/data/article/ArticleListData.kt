package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章列表响应数据")
data class ArticleListData(
    @field:Schema(description = "文章列表")
    val items: List<ArticleItemData>,

    @field:Schema(description = "下一页游标，传入下一次请求的 cursorArticleId 和 cursorPublishTime", nullable = true)
    val nextCursor: ArticleCursorData?,

    @field:Schema(description = "是否还有下一页")
    val hasMore: Boolean,
)
