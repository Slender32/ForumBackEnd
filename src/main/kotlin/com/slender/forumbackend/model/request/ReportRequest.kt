package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "举报请求")
data class ReportRequest(
    @field:Schema(description = "举报理由")
    val reason: ReportReason,
    @field:Schema(description = "补充说明。reason 为 Other 时必填")
    @field:Size(max = 500, message = "补充说明不能超过500字")
    val detail: String = "",
)

@Schema(description = "举报理由")
enum class ReportReason {
    Spam,
    Pornography,
    Abuse,
    Infringement,
    Illegal,
    Other,
}
