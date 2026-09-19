package com.slender.forumbackend.model.data.file

data class ImageMetadataData(
    val originalName: String,
    val width: Int,
    val height: Int,
    val byteSize: Long,
    val type: String,
)
