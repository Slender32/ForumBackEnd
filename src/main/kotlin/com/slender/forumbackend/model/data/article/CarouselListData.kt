package com.slender.forumbackend.model.data.article

import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "首页轮播列表")
data class CarouselListData(
    val items: List<CarouselItemData>,
)

@Schema(description = "轮播项")
data class CarouselItemData(
    val id: Long,
    val title: String,
    val summary: String,
    val image: String,
    val targetType: CarouselTargetType,
    val targetValue: String = "",
)
