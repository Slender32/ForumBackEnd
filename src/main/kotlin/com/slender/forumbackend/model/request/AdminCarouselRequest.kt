package com.slender.forumbackend.model.request

import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AdminCarouselRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @field:Size(max = 512)
    val summary: String = "",

    @field:NotBlank
    @field:Size(max = 1024)
    val image: String,

    val targetType: CarouselTargetType = CarouselTargetType.None,

    @field:Size(max = 512)
    val targetValue: String = "",

    val sortOrder: Int = 0,
    val enabled: Boolean = true,
)
