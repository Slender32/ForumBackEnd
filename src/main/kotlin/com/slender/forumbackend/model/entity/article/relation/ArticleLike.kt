package com.slender.forumbackend.model.entity.article.relation

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("article_likes")
data class ArticleLike(
    val articleId: Long,
    val userId: Long,
    val createTime: LocalDateTime,
)
