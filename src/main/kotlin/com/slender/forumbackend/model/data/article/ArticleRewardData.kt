package com.slender.forumbackend.model.data.article

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文章打赏结果")
data class ArticleRewardData(
    @field:Schema(description = "打赏后该文章的总打赏数")
    val rewardCount: Int,
    @field:Schema(description = "打赏后当前用户剩余的萌萌点")
    val remainingMoePoint: Int,
)
