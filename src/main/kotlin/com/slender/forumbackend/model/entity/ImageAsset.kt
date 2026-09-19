package com.slender.forumbackend.model.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("image_assets")
data class ImageAsset(
    @TableId
    val imageId: Long = 0,
    val url: String,
    val objectKey: String,
    val originalName: String,
    val width: Int,
    val height: Int,
    val byteSize: Long,
    val type: String,
    val createTime: LocalDateTime
)
