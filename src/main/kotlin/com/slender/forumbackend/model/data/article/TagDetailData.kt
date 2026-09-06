package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "标签详情")
data class TagDetailData(
    val tid: Long,
    val name: String,
    val color: Int,
    val articleCount: Int,
    val description: String = "",
)

@Schema(description = "标签列表")
data class TagListData(
    val items: List<ArticleTagData>,
)
