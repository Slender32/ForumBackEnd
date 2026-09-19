package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("website_introductions")
@Schema(description = "官网介绍记录")
data class WebsiteIntroduction(
    @TableId("introduction_id")
    @field:Schema(description = "记录 ID")
    val id: Long,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "详细说明")
    val description: String,
    @field:Schema(description = "图片访问地址")
    val imageUrl: String,
    @field:Schema(description = "展示排序值")
    val sortOrder: Int,
    @field:Schema(description = "是否启用")
    val enabled: Boolean,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
)
