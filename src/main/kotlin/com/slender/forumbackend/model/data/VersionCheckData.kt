package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "客户端版本检查结果")
data class VersionCheckData(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val latestBuildNumber: Long? = null,
    val forceUpdate: Boolean = false,
    val changelog: String = "",
    val downloadUrl: String = "",
    val publishedAt: Long? = null,
)
