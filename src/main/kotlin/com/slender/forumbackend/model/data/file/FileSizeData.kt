package com.slender.forumbackend.model.data.file

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文件大小响应数据")
data class FileSizeData(
    @field:Schema(description = "文件大小，单位为字节", example = "2048", minimum = "0")
    val size: Long,
)
