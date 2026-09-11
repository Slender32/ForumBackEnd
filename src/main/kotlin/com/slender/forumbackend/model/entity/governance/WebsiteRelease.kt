package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("website_releases")
data class WebsiteRelease(
    @TableId("release_id")
    val id: Long,
    val platform: String,
    val version: String,
    val title: String,
    val releaseNotes: String,
    val sha256: String,
    val downloadUrl: String,
    val releaseDate: LocalDateTime,
    val enabled: Boolean,
    val deletedAt: LocalDateTime? = null,
    val createTime: LocalDateTime = LocalDateTime.MIN,
    val updateTime: LocalDateTime = LocalDateTime.MIN,
)
