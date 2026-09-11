package com.slender.forumbackend.model.entity.favorite

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("favorites")
data class Favorite(
    @TableId
    val favoriteId: Long = 0, //TODO Review
    val userId: Long,
    val articleId: Long,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
