package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("website_introductions")
data class WebsiteIntroduction(
    @TableId("introduction_id")
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
    val sortOrder: Int,
    val enabled: Boolean,
    val deletedAt: LocalDateTime? = null,
    val createTime: LocalDateTime = LocalDateTime.MIN,
    val updateTime: LocalDateTime = LocalDateTime.MIN,
)
