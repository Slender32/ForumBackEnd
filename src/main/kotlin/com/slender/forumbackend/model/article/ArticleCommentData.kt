package com.slender.forumbackend.model.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章评论信息")
data class ArticleCommentData(
    @field:Schema(description = "评论ID", example = "1")
    val commentId: Long,

    @field:Schema(description = "评论作者信息")
    val author: ArticleUserData,

    @field:Schema(description = "发布时间戳，单位毫秒", example = "1767225600000")
    val publishTime: Long,

    @field:Schema(description = "评论内容", example = "写得不错")
    val content: String,

    @field:Schema(description = "点赞数", example = "0")
    val likeCount: Int,
)
