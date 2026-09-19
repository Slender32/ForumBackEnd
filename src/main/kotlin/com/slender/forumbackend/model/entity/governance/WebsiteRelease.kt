package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("website_releases")
@Schema(description = "客户端发布版本记录")
data class WebsiteRelease(
    @TableId("release_id")
    @field:Schema(description = "记录 ID")
    val id: Long,
    @field:Schema(description = "发布平台：Windows 或 Android")
    val platform: String,
    @field:Schema(description = "点分隔的非负整数版本号，按数字逐段比较，例如 1.10 大于 1.9")
    val version: String,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "版本更新说明")
    val releaseNotes: String,
    @field:Schema(description = "安装包 SHA-256 校验值，64 位十六进制字符串")
    val sha256: String,
    @field:Schema(description = "安装包下载地址")
    val downloadUrl: String,
    @field:Schema(description = "版本发布时间，格式 yyyy-MM-ddTHH:mm:ss")
    val releaseDate: LocalDateTime,
    @field:Schema(description = "是否启用")
    val enabled: Boolean,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
)
