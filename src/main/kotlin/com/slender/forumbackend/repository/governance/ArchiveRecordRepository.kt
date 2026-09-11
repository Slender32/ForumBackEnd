package com.slender.forumbackend.repository.governance

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.ArchiveRecordMapper
import com.slender.forumbackend.model.entity.governance.ArchiveRecord
import org.springframework.stereotype.Repository

@Repository
class ArchiveRecordRepository(
    private val mapper: ArchiveRecordMapper,
) : ServiceImpl<ArchiveRecordMapper, ArchiveRecord>(),
    IService<ArchiveRecord> {

    fun page(
        page: Int,
        size: Int,
        status: String?,
        sourceTable: String?,
    ): List<Map<String, Any?>> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        val query =
            query(status, sourceTable)
                .last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        return mapper.selectMaps(query)
    }

    fun count(status: String?, sourceTable: String?): Long =
        mapper.selectCount(filter(status, sourceTable))

    fun findMap(id: Long): Map<String, Any?>? =
        mapper.selectMaps(
            QueryWrapper<ArchiveRecord>()
                .eq("archive_id", id)
        ).firstOrNull()

    fun findBatch(batchId: String): List<Map<String, Any?>> =
        mapper.selectMaps(
            QueryWrapper<ArchiveRecord>()
                .eq("batch_id", batchId)
                .orderByAsc("source_table")
                .orderByAsc("source_id")
        )

    private fun query(
        status: String?,
        sourceTable: String?,
    ): QueryWrapper<ArchiveRecord> =
        QueryWrapper<ArchiveRecord>()
            .eq(!status.isNullOrBlank(), "status", status?.uppercase())
            .eq(!sourceTable.isNullOrBlank(), "source_table", sourceTable)
            .orderByDesc("create_time")

    private fun filter(
        status: String?,
        sourceTable: String?,
    ): QueryWrapper<ArchiveRecord> =
        QueryWrapper<ArchiveRecord>()
            .eq(!status.isNullOrBlank(), "status", status?.uppercase())
            .eq(!sourceTable.isNullOrBlank(), "source_table", sourceTable)
}
