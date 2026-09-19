package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive

@Schema(description = "添加用户标签请求")
data class UserTagRequest(
    @field:Schema(description = "标签 ID")
    @field:Positive val tagId: Long
)
