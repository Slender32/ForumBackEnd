package com.slender.forumbackend.model.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("tags")
data class Tag(
    @TableId
    val tid: Long = 0,
    val name: String,
    val color: Int = -1,
    val createTime: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
)
