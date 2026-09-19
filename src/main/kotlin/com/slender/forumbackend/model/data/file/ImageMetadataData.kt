package com.slender.forumbackend.model.data.file

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "图片元数据")
data class ImageMetadataData(
    @field:Schema(description = "上传时的原始文件名")
    val originalName: String,
    @field:Schema(description = "图片宽度，单位像素")
    val width: Int,
    @field:Schema(description = "图片高度，单位像素")
    val height: Int,
    @field:Schema(description = "图片文件大小，单位字节")
    val byteSize: Long,
    @field:Schema(description = "图片格式")
    val type: String,
)
