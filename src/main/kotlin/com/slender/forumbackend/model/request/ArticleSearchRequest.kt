package com.slender.forumbackend.model.request

import com.slender.forumbackend.validation.ArticleSearchCursor
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "文章搜索请求")
@ArticleSearchCursor
data class ArticleSearchRequest(
    @field:Schema(description = "搜索关键词", example = "kotlin")
    @field:NotBlank(message = "搜索关键词不能为空")
    @field:Size(max = 64, message = "搜索关键词不能超过64字")
    val keyword: String,

    @field:Schema(description = "游标文章ID，首次请求传-1", example = "-1")
    @field:Min(value = -1, message = "游标文章ID不能小于-1")
    val cursorArticleId: Long = -1L,

    @field:Schema(description = "游标文章发布时间戳，首次请求不传", nullable = true)
    @field:Min(value = 0, message = "游标文章发布时间不能小于0")
    val cursorPublishTime: Long? = null,

    @field:Schema(description = "每页数量，范围1到10", example = "10")
    @field:Min(value = 1, message = "每页数量不能小于1")
    @field:Max(value = 10, message = "每页数量不能大于10")
    val size: Int = 10,
)
