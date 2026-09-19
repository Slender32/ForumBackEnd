package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "修改文章请求")
data class ArticleUpdateRequest(
    @field:NotBlank
    @field:Size(max = 255)
    @field:Schema(description = "标题")
    val title: String,

    @field:Size(max = 512)
    @field:Schema(description = "摘要")
    val summary: String = "",

    @field:NotBlank
    @field:Schema(description = "文章正文，Markdown 格式")
    val content: String,

    @field:Size(max = 1024)
    @field:Schema(description = "封面图片地址")
    val cover: String = "",

    @field:Valid
    @field:Size(max = 10)
    @field:Schema(description = "完整标签列表；null 表示保持原标签，空列表表示清空")
    val tags: List<ArticlePublishTagRequest>? = null,
)
