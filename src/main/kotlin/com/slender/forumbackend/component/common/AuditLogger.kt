package com.slender.forumbackend.component.common

import com.slender.forumbackend.model.entity.governance.AuditLogRecord
import com.slender.forumbackend.repository.governance.AuditLogRepository
import org.springframework.stereotype.Component

@Component
class AuditLogger(
    private val repository: AuditLogRepository
) {
    private val sequence = ThreadLocal.withInitial { 0L }

    fun log(
        operatorId: Long?,
        permission: String,
        resource: String,
        resourceId: String?,
        action: String,
        summary: String? = null,
        result: String = "SUCCESS",
    ) {
        repository.append(
            AuditLogRecord(
                operatorId = operatorId,
                permissionCode = permission,
                resourceType = resource,
                resourceId = resourceId,
                action = action,
                requestSummary = summary?.take(2000),
                result = result,
            )
        )
        sequence.set(sequence.get() + 1)
    }

    fun currentSequence(): Long = sequence.get()
}
