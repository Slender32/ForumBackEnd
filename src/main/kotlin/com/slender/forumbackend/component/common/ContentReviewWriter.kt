package com.slender.forumbackend.component.common

import com.slender.forumbackend.model.entity.governance.ContentReviewRecord
import com.slender.forumbackend.repository.governance.ContentReviewRepository
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class ContentReviewWriter(
    private val repository: ContentReviewRepository,
    private val mapper: ObjectMapper
) {
    fun enqueue(
        resourceType: String,
        resourceId: Long?,
        authorId: Long,
        payload: Any,
        content: String? = null,
    ): Long {
        val json = mapper.writeValueAsString(payload)
        return repository.enqueue(
            ContentReviewRecord(
                resourceType = resourceType,
                resourceId = resourceId?.toString(),
                authorId = authorId,
                content = content,
                payloadJson = json,
            )
        )
    }
}