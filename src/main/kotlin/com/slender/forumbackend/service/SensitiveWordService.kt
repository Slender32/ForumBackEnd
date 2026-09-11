package com.slender.forumbackend.service

import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.governance.SensitiveWordData
import com.slender.forumbackend.model.request.SensitiveWordRequest
import org.springframework.stereotype.Service

@Service
class SensitiveWordService(
    private val query: SensitiveWordQueryService,
    private val command: SensitiveWordCommandService,
) {
    fun evaluate(input: String) = query.evaluate(input)

    fun adminWords(
        page: Int,
        size: Int,
        includeDeleted: Boolean,
        keyword: String? = null,
        enabled: Boolean? = null,
    ): AdminPageData<SensitiveWordData> = query.adminWords(page, size, includeDeleted, keyword, enabled)

    fun add(req: SensitiveWordRequest, op: Long) = command.add(req, op)

    fun update(id: Long, req: SensitiveWordRequest, op: Long) = command.update(id, req, op)

    fun delete(id: Long, op: Long) = command.delete(id, op)

    fun invalidateForTests() = command.invalidate()
}
