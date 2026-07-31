package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.IdType.INPUT
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("article_stats")
data class ArticleStatistic(
    @TableId(type = INPUT)
    val articleId: Long,
    val likeCount: Int,
    val commentCount: Int,
    val viewCount: Int,
)
