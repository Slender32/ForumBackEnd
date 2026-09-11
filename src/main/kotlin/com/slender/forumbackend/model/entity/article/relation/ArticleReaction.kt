package com.slender.forumbackend.model.entity.article.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("article_reactions")
data class ArticleReaction(
    val articleId: Long,
    val userId: Long,
    val emoji: String,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
