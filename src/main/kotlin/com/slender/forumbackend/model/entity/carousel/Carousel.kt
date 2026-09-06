package com.slender.forumbackend.model.entity.carousel

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import java.time.LocalDateTime

@TableName("carousels")
data class Carousel(
    @TableId
    val carouselId: Long = 0,
    val title: String,
    val summary: String,
    val image: String,
    val targetType: CarouselTargetType,
    val targetValue: String,
    val sortOrder: Int = 0,
    val enabled: Boolean = true,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
)
