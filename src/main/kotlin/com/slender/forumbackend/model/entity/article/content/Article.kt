package com.slender.forumbackend.model.entity.article.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.data.article.ArticleCursorData
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@TableName("articles")
@Schema(description = "管理端文章记录")
data class Article(
    @TableId
    @field:Schema(description = "文章 ID")
    val articleId: Long = 0,
    @field:Schema(description = "作者用户 ID")
    val authorId: Long,
    @field:Schema(description = "标题")
    val title: String,
    @field:Schema(description = "摘要")
    val summary: String,
    @field:Schema(description = "封面图片地址")
    val cover: String,
    @field:Schema(description = "状态")
    val status: ArticleStatus,
    @field:Schema(description = "文章可见范围")
    val visibility: ArticleVisibility,
    @field:Schema(description = "发布时间，格式 yyyy-MM-ddTHH:mm:ss")
    val publishTime: LocalDateTime,
    @field:Schema(description = "最后修订时间，格式 yyyy-MM-ddTHH:mm:ss")
    val reviseTime: LocalDateTime,
    @field:Schema(description = "创建时间，格式 yyyy-MM-ddTHH:mm:ss")
    val createTime: LocalDateTime,
    @field:Schema(description = "最后更新时间，格式 yyyy-MM-ddTHH:mm:ss")
    val updateTime: LocalDateTime,
    @field:Schema(description = "软删除时间；未删除时为 null")
    val deletedAt: LocalDateTime? = null,
) {
    fun toCursorData() = ArticleCursorData(
        articleId = articleId,
        publishTime = publishTime.timestamp,
    )
}
