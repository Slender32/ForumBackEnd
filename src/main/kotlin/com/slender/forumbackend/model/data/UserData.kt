package com.slender.forumbackend.model.data

import com.slender.forumbackend.constant.enumeration.user.Gender
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户展示信息")
data class UserData(
    @field:Schema(description = "用户ID", example = "1")
    val uid: Long,

    @field:Schema(description = "用户名", example = "slender")
    val name: String,

    @field:Schema(description = "邮箱", example = "user@example.com")
    val email: String,

    @field:Schema(description = "头像地址", example = "https://example.com/avatar.png")
    val avatar: String,

    @field:Schema(description = "性别")
    val gender: Gender,

    @field:Schema(description = "个性签名", example = "愿代码也有风骨")
    val signature: String,

    @field:Schema(description = "创建时间戳，单位毫秒", example = "1767225600000")
    val createTime: Long,

    @field:Schema(description = "粉丝数", example = "0")
    val fanCount: Int,

    @field:Schema(description = "关注数", example = "0")
    val followCount: Int,

    @field:Schema(description = "已发布文章数", example = "0")
    val publishedArticleCount: Int,

    @field:Schema(description = "获赞数", example = "0")
    val likedCount: Int,
)
