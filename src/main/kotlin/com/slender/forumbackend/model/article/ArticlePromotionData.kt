package com.slender.forumbackend.model.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章推荐信息")
data class ArticlePromotionData(
    @field:Schema(description = "推荐ID", example = "1")
    val promotionId: Long,

    @field:Schema(description = "推荐人信息")
    val promoter: ArticleUserData,

    @field:Schema(description = "推荐时间戳，单位毫秒", example = "1767225600000")
    val promoteTime: Long,

    @field:Schema(description = "推荐内容", example = "值得一看")
    val content: String,
)
