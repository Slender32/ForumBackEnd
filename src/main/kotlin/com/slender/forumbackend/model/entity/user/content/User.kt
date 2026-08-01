package com.slender.forumbackend.model.entity.user.content

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.constant.enumeration.user.UserStatus
import com.slender.forumbackend.library.timestamp
import com.slender.forumbackend.model.article.ArticleUserData
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.constant.enumeration.user.Gender
import java.time.LocalDateTime

@TableName("users")
data class User(
    @TableId
    val uid: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val avatar: String,
    val level: Int = 0,
    val gender: Gender,
    val signature: String,
    val status: UserStatus,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
){
    fun toUserData(statistics: UserStatistics? = null) = UserData(
        uid = uid,
        name = name,
        email = email,
        avatar = avatar,
        level = level,
        gender = gender,
        signature = signature,
        createTime = createTime.timestamp,
        fanCount = statistics?.fanCount ?: 0,
        followCount = statistics?.followCount ?: 0,
        publishedArticleCount = statistics?.publishedArticleCount ?: 0,
        likedCount = statistics?.likedCount ?: 0,
    )

    fun toArticleUserData() = ArticleUserData(uid, name, avatar, level)
}
