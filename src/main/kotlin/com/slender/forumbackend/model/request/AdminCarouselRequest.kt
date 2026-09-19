package com.slender.forumbackend.model.request

import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "新增或修改首页轮播请求")
data class AdminCarouselRequest(
    @field:NotBlank
    @field:Size(max = 255)
    @field:Schema(description = "标题")
    val title: String,

    @field:Size(max = 512)
    @field:Schema(description = "摘要")
    val summary: String = "",

    @field:NotBlank
    @field:Size(max = 1024)
    @field:Schema(description = "图片访问地址")
    val image: String,

    @field:Schema(description = "轮播跳转目标类型")
    val targetType: CarouselTargetType = CarouselTargetType.None,

    @field:Size(max = 512)
    @field:Schema(description = "跳转目标值，含义由 targetType 决定")
    val targetValue: String = "",

    @field:Schema(description = "展示排序值")
    val sortOrder: Int = 0,
    @field:Schema(description = "是否启用")
    val enabled: Boolean = true,
)
