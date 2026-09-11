package com.slender.forumbackend.facade

import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.data.governance.SensitiveWordData
import com.slender.forumbackend.model.request.SensitiveWordRequest
import com.slender.forumbackend.service.SensitiveWordService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminSensitiveWordFacade(
    private val service: SensitiveWordService
) {
    fun list(
        page: Int,
        size: Int,
        includeDeleted: Boolean,
        keyword: String?,
        enabled: Boolean?,
    ): AdminPageData<SensitiveWordData> =
        service.adminWords(page, size, includeDeleted, keyword, enabled)

    @Transactional
    fun add(request: SensitiveWordRequest, operator: Long) = service.add(request, operator)

    @Transactional
    fun update(id: Long, request: SensitiveWordRequest, operator: Long) = service.update(id, request, operator)

    @Transactional
    fun delete(id: Long, operator: Long) = service.delete(id, operator)
}
