package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.model.data.governance.ContentReviewData
import java.time.LocalDateTime

@TableName("content_reviews")
data class ContentReviewRecord(
    @TableId
    val reviewId: Long = 0, //TODO Review
    val resourceType: String,
    val resourceId: String? = null,
    val authorId: Long,
    val content: String? = null,
    val payloadJson: String? = null,
    val status: String = "PENDING",
    val reviewerId: Long? = null,
    val reviewNote: String? = null,
    val createTime: LocalDateTime? = null,
    val updateTime: LocalDateTime? = null,
){
    fun toData() = ContentReviewData(
        reviewId = reviewId,
        resourceType = resourceType,
        resourceId = resourceId,
        authorId = authorId,
        content = content,
        payloadJson = payloadJson,
        status = status,
        reviewerId = reviewerId,
        reviewNote = reviewNote,
        createTime = createTime,
        updateTime = updateTime,
    )
}
