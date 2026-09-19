package com.slender.forumbackend.model.data.website

import com.slender.forumbackend.constant.enumeration.website.WebsitePlatform
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "官网下载版本列表")
data class WebsiteReleaseData(
    @field:Schema(description = "当前页数据列表")
    val items: List<WebsiteReleaseItemData>,
)

@Schema(description = "官网下载版本")
data class WebsiteReleaseItemData(
    @field:Schema(description = "发布平台：Windows 或 Android")
    val platform: WebsitePlatform,
    @field:Schema(description = "点分隔的非负整数版本号，按数字逐段比较，例如 1.10 大于 1.9")
    val version: String,
    @field:Schema(description = "安装包 SHA-256 校验值，64 位十六进制字符串")
    val sha256: String,
    @field:Schema(description = "安装包下载地址")
    val downloadUrl: String,
)
