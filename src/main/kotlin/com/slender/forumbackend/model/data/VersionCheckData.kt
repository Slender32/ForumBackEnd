package com.slender.forumbackend.model.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "客户端版本检查结果")
data class VersionCheckData(
    @field:Schema(description = "服务器最高有效版本是否高于当前客户端版本")
    val hasUpdate: Boolean,
    @field:Schema(description = "服务器最高有效版本；没有可用发布版本时返回客户端当前版本")
    val latestVersion: String,
    @field:Schema(description = "兼容保留字段，当前返回 null，不参与版本比较")
    val latestBuildNumber: Long? = null,
    @field:Schema(description = "是否强制更新；当前固定为 false")
    val forceUpdate: Boolean = false,
    @field:Schema(description = "版本更新说明；没有可用发布版本时为空字符串")
    val changelog: String = "",
    @field:Schema(description = "最高有效版本下载地址；没有可用发布版本时为空字符串")
    val downloadUrl: String = "",
    @field:Schema(description = "最高有效版本发布时间戳，单位毫秒；没有可用发布版本时为 null")
    val publishedAt: Long? = null,
)
