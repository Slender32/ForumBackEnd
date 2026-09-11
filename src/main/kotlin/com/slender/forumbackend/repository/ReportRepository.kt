package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.exception.ReportDuplicatedException
import com.slender.forumbackend.mapper.ReportMapper
import com.slender.forumbackend.model.entity.report.Report
import java.time.LocalDateTime
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository

@Repository
class ReportRepository(
    private val reportMapper: ReportMapper
) : ServiceImpl<ReportMapper, Report>(), IService<Report> {
    fun insert(report: Report) {
        try {
            reportMapper.insert(report)
        } catch (_: DuplicateKeyException) {
            throw ReportDuplicatedException()
        }
    }

    fun list(page: Int, size: Int, includeDeleted: Boolean = false): List<Report> =
        reportMapper.selectList(
            QueryWrapper<Report>()
                .apply(if (!includeDeleted) "deleted_at IS NULL" else "1=1")
                .orderByDesc("create_time")
                .last(
                    "LIMIT ${size.coerceIn(1, 100)} OFFSET ${(page.coerceAtLeast(1) - 1) * size.coerceIn(1, 100)}"
                )
        )

    fun count(includeDeleted: Boolean = false): Long =
        reportMapper.selectCount(
            QueryWrapper<Report>().apply(if (!includeDeleted) "deleted_at IS NULL" else "1=1")
        )

    fun find(id: Long): Report? = reportMapper.selectById(id)

    fun update(report: Report): Boolean = reportMapper.updateById(report) > 0

    fun markDeleted(id: Long, now: LocalDateTime): Boolean =
        reportMapper.update(
            null,
            com.baomidou.mybatisplus.core.conditions.update
                .UpdateWrapper<Report>()
                .eq("report_id", id)
                .isNull("deleted_at")
                .set("deleted_at", now)
                .set("update_time", now),
        ) > 0
}
