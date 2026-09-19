package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("article_promotions")
@Schema(description = "管理端文章推荐记录")
data class ArticlePromotion(
    @TableId
    @field:Schema(description = "文章推荐 ID")
    val promotionId: Long = 0,
    @field:Schema(description = "文章 ID")
    val articleId: Long,
    @field:Schema(description = "推荐人用户 ID")
    val promoterId: Long,
    @field:Schema(description = "正文内容")
    val content: String,
    @field:Schema(description = "推荐时间，格式 yyyy-MM-ddTHH:mm:ss")
    val promoteTime: LocalDateTime,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
)
