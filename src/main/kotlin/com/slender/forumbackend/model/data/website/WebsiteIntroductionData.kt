package com.slender.forumbackend.model.data.website

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "官网介绍轮播列表")
data class WebsiteIntroductionData(
    @field:Schema(description = "当前页数据列表")
    val items: List<WebsiteIntroductionItemData>,
)

@Schema(description = "官网介绍轮播项")
data class WebsiteIntroductionItemData(
    @field:Schema(description = "记录 ID")
    val id: Long,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "详细说明")
    val description: String,
    @field:Schema(description = "图片访问地址")
    val imageUrl: String,
)
