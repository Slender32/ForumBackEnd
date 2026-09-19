package com.slender.forumbackend.model.data.article

import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "首页轮播列表")
data class CarouselListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<CarouselItemData>,
)

@Schema(description = "轮播项")
data class CarouselItemData(
    @field:Schema(description = "记录 ID")
    val id: Long,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "摘要")
    val summary: String,
    @field:Schema(description = "图片访问地址")
    val image: String,
    @field:Schema(description = "轮播跳转目标类型")
    val targetType: CarouselTargetType,
    @field:Schema(description = "跳转目标值，含义由 targetType 决定")
    val targetValue: String = "",
)
