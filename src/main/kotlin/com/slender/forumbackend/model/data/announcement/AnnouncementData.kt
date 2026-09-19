package com.slender.forumbackend.model.data.announcement

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "公告列表及未读数量")
data class AnnouncementListData(
    @field:Schema(description = "当前页数据列表")
    val items: List<AnnouncementData>,
    @field:Schema(description = "展示位置下全部可见公告的未读数，匿名访问时为 null")
    val unreadCount: Long?,
)

@Schema(description = "公告详情及当前用户阅读状态")
data class AnnouncementData(
    @field:Schema(description = "记录 ID")
    val id: Long,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "摘要")
    val summary: String,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "发布时间，Unix 毫秒时间戳")
    val publishedAt: Long,
    @field:Schema(description = "是否置顶")
    @get:Schema(description = "是否置顶")
    val isPinned: Boolean,
    @field:Schema(description = "公告面向的用户范围")
    val target: String,
    @field:Schema(description = "当前用户是否已读，匿名访问时为 null")
    @get:Schema(description = "当前用户是否已读，匿名访问时为 null")
    val isRead: Boolean?,
)
