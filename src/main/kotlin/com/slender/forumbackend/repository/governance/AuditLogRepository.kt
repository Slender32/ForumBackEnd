package com.slender.forumbackend.repository.governance

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.AuditLogMapper
import com.slender.forumbackend.model.entity.governance.AuditLogRecord
import org.springframework.stereotype.Repository

@Repository
class AuditLogRepository(
    private val mapper: AuditLogMapper,
) : ServiceImpl<AuditLogMapper, AuditLogRecord>(),
    IService<AuditLogRecord> {

    fun append(record: AuditLogRecord) = mapper.insert(record)

    fun page(
        page: Int,
        size: Int,
        result: String?,
        resourceType: String?,
    ): List<Map<String, Any?>> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        val query =
            query(result, resourceType)
                .last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        return mapper.selectMaps(query)
    }

    fun count(result: String?, resourceType: String?): Long =
        mapper.selectCount(filter(result, resourceType))

    private fun query(
        result: String?,
        resourceType: String?,
    ): QueryWrapper<AuditLogRecord> =
        QueryWrapper<AuditLogRecord>()
            .eq(!result.isNullOrBlank(), "result", result?.uppercase())
            .eq(!resourceType.isNullOrBlank(), "resource_type", resourceType)
            .orderByDesc("create_time")

    private fun filter(
        result: String?,
        resourceType: String?,
    ): QueryWrapper<AuditLogRecord> =
        QueryWrapper<AuditLogRecord>()
            .eq(!result.isNullOrBlank(), "result", result?.uppercase())
            .eq(!resourceType.isNullOrBlank(), "resource_type", resourceType)
}
