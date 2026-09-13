package com.slender.forumbackend.model.data

import com.slender.forumbackend.model.entity.article.content.Article
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility
import java.time.LocalDateTime

/** Flat, backwards-compatible article metadata plus the body required by the admin editor. */
data class AdminArticleDetailData(
    val articleId: Long,
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
    val deletedAt: LocalDateTime?,
    val content: String?,
) {
    constructor(article: Article, content: String?) : this(article.articleId, article.authorId, article.title, article.summary, article.cover, article.status, article.visibility, article.publishTime, article.reviseTime, article.createTime, article.updateTime, article.deletedAt, content)
}
