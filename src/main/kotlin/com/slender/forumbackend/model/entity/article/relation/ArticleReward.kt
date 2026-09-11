package com.slender.forumbackend.model.entity.article.relation

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("article_rewards")
data class ArticleReward(
    @TableId
    val rewardId: Long = 0,
    val articleId: Long,
    val userId: Long,
    val amount: Int,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
