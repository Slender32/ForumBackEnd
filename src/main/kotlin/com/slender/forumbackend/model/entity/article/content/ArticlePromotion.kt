package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("article_promotions")
data class ArticlePromotion(
    @TableId
    val promotionId: Long = 0,
    val articleId: Long,
    val promoterId: Long,
    val content: String,
    val promoteTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
