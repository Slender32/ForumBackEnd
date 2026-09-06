package com.slender.forumbackend.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "修改头像请求")
data class UpdateAvatarRequest(
    @field:Schema(description = "图片 URL；图片上传复用文件模块接口")
    @field:NotBlank(message = "头像地址不能为空")
    @field:Size(max = 1024, message = "头像地址不能超过1024字符")
    val avatar: String,
)
