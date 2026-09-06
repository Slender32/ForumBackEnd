package com.slender.forumbackend.model.data.file

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "文件上传响应数据")
data class FileData(
    @field:Schema(
        description = "文件访问地址",
        example = "https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/2026/08/example.png"
    )
    val url: String
)