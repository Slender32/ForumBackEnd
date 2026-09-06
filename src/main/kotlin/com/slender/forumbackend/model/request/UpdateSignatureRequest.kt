package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "修改签名请求")
data class UpdateSignatureRequest(
    @field:Schema(description = "个性签名，允许空字符串清空")
    @field:Size(max = 120, message = "签名不能超过120字符")
    val signature: String = "",
)
