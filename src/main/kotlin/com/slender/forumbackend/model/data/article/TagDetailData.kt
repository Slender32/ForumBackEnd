package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "标签详情")
data class TagDetailData(
    @field:Schema(description = "标签 ID")
    val tid: Long,
    @field:Schema(description = "名称")
    val name: String,
    @field:Schema(description = "32 位 ARGB 颜色值，使用有符号整数表示")
    val color: Int,
    @field:Schema(description = "文章数量")
    val articleCount: Int,
    @field:Schema(description = "详细说明")
    val description: String = "",
)

@Schema(description = "标签列表")
data class TagListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<ArticleTagData>,
)
