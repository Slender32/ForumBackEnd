package com.slender.forumbackend.model.entity.carousel

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.carousel.CarouselTargetType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("carousels")
@Schema(description = "管理端首页轮播记录")
data class Carousel(
    @TableId
    @field:Schema(description = "轮播 ID")
    val carouselId: Long = 0,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "摘要")
    val summary: String,
    @field:Schema(description = "图片访问地址")
    val image: String,
    @field:Schema(description = "轮播跳转目标类型")
    val targetType: CarouselTargetType,
    @field:Schema(description = "跳转目标值，含义由 targetType 决定")
    val targetValue: String,
    @field:Schema(description = "展示排序值")
    val sortOrder: Int = 0,
    @field:Schema(description = "是否启用")
    val enabled: Boolean = true,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
)
