package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章详情数据")
data class ArticleDetailData(
    @field:Schema(description = "文章ID")
    val articleId: Long,

    @field:Schema(description = "文章标题")
    val title: String,

    @field:Schema(description = "摘要")
    val summary: String = "",

    @field:Schema(description = "封面图片地址")
    val cover: String = "",

    @field:Schema(description = "Markdown内容")
    val content: String,

    @field:Schema(description = "作者信息")
    val author: ArticleUserData,

    @field:Schema(description = "标签列表")
    val tags: List<ArticleTagData>,

    @field:Schema(description = "发布时间(毫秒时间戳)")
    val publishTime: Long,

    @field:Schema(description = "最后编辑时间(毫秒时间戳)")
    val reviseTime: Long,

    @field:Schema(description = "浏览数")
    val viewCount: Int,

    @field:Schema(description = "点赞数")
    val likeCount: Int,

    @field:Schema(description = "打赏(萌萌点)总数")
    val rewardCount: Long = 0,

    @field:Schema(description = "评论数")
    val commentCount: Int,

    @field:Schema(description = "表情回应聚合")
    val reactions: List<ArticleReactionData> = emptyList(),

    @field:Schema(description = "当前登录用户是否已点赞")
    @get:Schema(description = "当前登录用户是否已点赞")
    val isLike: Boolean = false,

    @field:Schema(description = "当前登录用户是否已打赏")
    @get:Schema(description = "当前登录用户是否已打赏")
    val isRewarded: Boolean = false,

    @field:Schema(description = "当前登录用户是否已收藏")
    @get:Schema(description = "当前登录用户是否已收藏")
    val isFavorite: Boolean = false,
)
