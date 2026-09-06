package com.slender.forumbackend.model.data.website

import com.slender.forumbackend.constant.enumeration.website.WebsitePlatform
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "官网下载版本列表")
data class WebsiteReleaseData(
    val items: List<WebsiteReleaseItemData>,
)

@Schema(description = "官网下载版本")
data class WebsiteReleaseItemData(
    val platform: WebsitePlatform,
    val version: String,
    val sha256: String,
    val downloadUrl: String,
)
