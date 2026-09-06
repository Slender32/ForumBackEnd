package com.slender.forumbackend.model.data.website

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "官网介绍轮播列表")
data class WebsiteIntroductionData(
    val items: List<WebsiteIntroductionItemData>,
)

@Schema(description = "官网介绍轮播项")
data class WebsiteIntroductionItemData(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
)
