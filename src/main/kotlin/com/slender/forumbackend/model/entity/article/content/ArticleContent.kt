package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.IdType.INPUT
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("article_contents")
data class ArticleContent(
    @TableId(type = INPUT)
    val articleId: Long,
    val content: String,
    val deletedAt: LocalDateTime? = null,
)
