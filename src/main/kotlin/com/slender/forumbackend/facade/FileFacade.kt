package com.slender.forumbackend.facade

import com.slender.forumbackend.model.data.file.FileData
import com.slender.forumbackend.model.data.file.FileSizeData
import com.slender.forumbackend.model.data.file.ImageMetadataData
import com.slender.forumbackend.mapper.ImageAssetMapper
import com.slender.forumbackend.model.entity.ImageAsset
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.slender.forumbackend.toolkit.FileStore
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileFacade(
    private val fileStore: FileStore,
    private val imageAssetMapper: ImageAssetMapper,
) {
    fun upload(file: MultipartFile) =
        FileData(fileStore.upload(file.originalFilename, file.bytes))

    fun uploadImages(files: List<MultipartFile>) =
        fileStore.uploadImages(files)

    fun size(name: String) =
        FileSizeData(fileStore.size(name))

    fun imageMetadata(url: String): ImageMetadataData? =
        imageAssetMapper
            .selectOne(QueryWrapper<ImageAsset>().eq("url", url))
            ?.let { asset ->
                ImageMetadataData(asset.originalName, asset.width, asset.height, asset.byteSize, asset.type)
            }
}
