package com.slender.forumbackend.model.entity.article.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("article_tags")
data class ArticleTag(
    val articleId: Long = 0,
    val tagId: Long = 0,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
