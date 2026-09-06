package com.slender.forumbackend.facade

import com.slender.forumbackend.model.data.file.FileData
import com.slender.forumbackend.model.data.file.FileSizeData
import com.slender.forumbackend.toolkit.FileStore
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileFacade(
    private val fileStore: FileStore,
) {
    fun upload(file: MultipartFile) =
        FileData(fileStore.upload(file.originalFilename, file.bytes))

    fun uploadImages(files: List<MultipartFile>) =
        fileStore.uploadImages(files)

    fun size(name: String) =
        FileSizeData(fileStore.size(name))
}
