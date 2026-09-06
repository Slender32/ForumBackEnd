package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticleCursorData
import java.time.LocalDateTime

@TableName("articles")
data class Article(
    @TableId
    val articleId: Long = 0,
    val authorId: Long,
    val title: String,
    val summary: String,
    val cover: String,
    val status: ArticleStatus,
    val visibility: ArticleVisibility,
    val publishTime: LocalDateTime,
    val reviseTime: LocalDateTime,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
) {
    fun toCursorData() = ArticleCursorData(
        articleId = articleId,
        publishTime = publishTime.timestamp,
    )
}
