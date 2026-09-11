package com.slender.forumbackend.model.request

import com.slender.forumbackend.constant.enumeration.report.ReportStatus
import jakarta.validation.constraints.Size

data class AdminReportUpdateRequest(
    val status: ReportStatus,
    @field:Size(max = 512) val resolutionNote: String? = null,
)
