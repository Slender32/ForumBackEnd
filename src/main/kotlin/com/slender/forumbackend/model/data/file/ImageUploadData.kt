package com.slender.forumbackend.model.data.file

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "批量图片上传结果")
data class ImageUploadData(
    @field:Schema(description = "当前页数据列表")
    val items: List<ImageUploadItemData>,
)

@Schema(description = "单张图片上传结果")
data class ImageUploadItemData(
    @field:Schema(description = "是否上传成功")
    val success: Boolean,
    @field:Schema(description = "文件访问地址")
    val url: String = "",
    @field:Schema(description = "上传失败原因；成功时为空字符串")
    val message: String = "",
)
