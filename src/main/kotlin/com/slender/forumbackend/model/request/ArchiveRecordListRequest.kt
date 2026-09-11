package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ArchiveRecordListRequest(
    @field:Min(1) val page: Int = 1,
    @field:Min(1) @field:Max(100) val size: Int = 20,
    val status: String? = null,
    val sourceTable: String? = null,
)
