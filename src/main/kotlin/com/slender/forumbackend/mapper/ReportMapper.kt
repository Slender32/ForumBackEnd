package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.report.Report
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ReportMapper : BaseMapper<Report>
