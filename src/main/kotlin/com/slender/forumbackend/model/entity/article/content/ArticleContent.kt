package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("article_contents")
data class ArticleContent(
    @TableId
    val articleId: Long,
    val content: String,
)
