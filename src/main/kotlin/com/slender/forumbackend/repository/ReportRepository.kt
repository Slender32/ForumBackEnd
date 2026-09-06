package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.exception.ReportDuplicatedException
import com.slender.forumbackend.mapper.ReportMapper
import com.slender.forumbackend.model.entity.report.Report
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository

@Repository
class ReportRepository(
    private val reportMapper: ReportMapper,
) : ServiceImpl<ReportMapper, Report>(), IService<Report> {
    fun insert(report: Report) {
        try {
            reportMapper.insert(report)
        } catch (_: DuplicateKeyException) {
            throw ReportDuplicatedException()
        }
    }
}
