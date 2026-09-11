package com.slender.forumbackend.repository.governance

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ContentReviewMapper
import com.slender.forumbackend.model.entity.governance.ContentReviewRecord
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ContentReviewRepository(
    private val mapper: ContentReviewMapper,
) : ServiceImpl<ContentReviewMapper, ContentReviewRecord>(), IService<ContentReviewRecord> {

    fun enqueue(record: ContentReviewRecord): Long {
        supersedePending(record.resourceType, record.resourceId)
        mapper.insert(record)
        return record.reviewId
    }

    fun supersedePending(resourceType: String, resourceId: String?): Int {
        if (resourceId.isNullOrBlank()) return 0
        return mapper.update(
            null,
            UpdateWrapper<ContentReviewRecord>()
                .eq("resource_type", resourceType)
                .eq("resource_id", resourceId)
                .eq("status", "PENDING")
                .set("status", "REJECTED")
                .set("review_note", "已被更新提交替代")
                .set("update_time", LocalDateTime.now()),
        )
    }

    fun list(
        status: String?,
        page: Int,
        size: Int,
    ): List<ContentReviewRecord> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        val query =
            QueryWrapper<ContentReviewRecord>()
                .eq(!status.isNullOrBlank(), "status", status)
                .orderByDesc("create_time")
                .last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        return mapper.selectList(query)
    }

    fun count(status: String?): Long =
        mapper.selectCount(
            QueryWrapper<ContentReviewRecord>()
                .eq(!status.isNullOrBlank(), "status", status)
        )

    fun find(id: Long): ContentReviewRecord? = mapper.selectById(id)

    fun findPendingForUpdate(id: Long): ContentReviewRecord? =
        mapper.selectForUpdate(id)

    fun updateDecision(
        id: Long,
        reviewerId: Long,
        status: String,
        note: String?,
    ): Int =
        mapper.update(
            null,
            UpdateWrapper<ContentReviewRecord>()
                .eq("review_id", id)
                .eq("status", "PENDING")
                .set("status", status)
                .set("reviewer_id", reviewerId)
                .set("review_note", note)
                .set("update_time", LocalDateTime.now()),
        )
}
