package com.slender.forumbackend.model.request

import com.slender.forumbackend.constant.enumeration.report.ReportStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "更新举报处理状态请求")
data class AdminReportUpdateRequest(
    @field:Schema(description = "状态")
    val status: ReportStatus,
    @field:Schema(description = "举报处理备注")
    @field:Size(max = 512) val resolutionNote: String? = null,
)
