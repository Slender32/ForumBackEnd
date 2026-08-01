package com.slender.forumbackend.model.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章卡片数据")
data class ArticleItemData(
    @field:Schema(description = "文章ID", example = "1")
    val articleId: Long,

    @field:Schema(description = "作者信息")
    val author: ArticleUserData,

    @field:Schema(description = "发布时间戳，单位毫秒", example = "1767225600000")
    val publishTime: Long,

    @field:Schema(description = "修订时间戳，单位毫秒", example = "1767225600000")
    val reviseTime: Long,

    @field:Schema(description = "标题", example = "一篇文章")
    val title: String,

    @field:Schema(description = "摘要", example = "这是摘要")
    val summary: String,

    @field:Schema(description = "封面地址", example = "https://example.com/cover.png")
    val cover: String,

    @field:Schema(description = "点赞数", example = "0")
    val likeCount: Int,

    @field:Schema(description = "评论数", example = "0")
    val commentCount: Int,

    @field:Schema(description = "浏览数", example = "0")
    val viewCount: Int,

    @field:Schema(description = "标签列表")
    val tags: List<ArticleTagData>,

    @field:Schema(description = "推荐列表")
    val promotions: List<ArticlePromotionData>,

    @field:Schema(description = "评论列表")
    val comments: List<ArticleCommentData>,

    @field:Schema(description = "表情反应列表")
    val reactions: List<ArticleReactionData>,
)
