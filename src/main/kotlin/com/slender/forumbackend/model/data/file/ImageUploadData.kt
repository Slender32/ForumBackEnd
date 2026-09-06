package com.slender.forumbackend.model.data.file

data class ImageUploadData(
    val items: List<ImageUploadItemData>,
)

data class ImageUploadItemData(
    val success: Boolean,
    val url: String = "",
    val message: String = "",
)
