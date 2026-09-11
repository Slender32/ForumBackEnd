package com.slender.forumbackend.toolkit

import com.aliyun.sdk.service.oss2.OSSClient
import com.aliyun.sdk.service.oss2.models.GetObjectMetaRequest
import com.aliyun.sdk.service.oss2.models.PutObjectRequest.newBuilder
import com.aliyun.sdk.service.oss2.transport.BinaryData.fromBytes
import com.slender.forumbackend.configuration.OSS.Companion.BUCKET_NAME
import com.slender.forumbackend.configuration.OSS.Companion.ENDPOINT
import com.slender.forumbackend.constant.core.Message.Exception.FILE_NAME_INVALID_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.IMAGE_EMPTY_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.IMAGE_TOO_LARGE_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.IMAGE_TYPE_UNSUPPORTED_ERROR
import com.slender.forumbackend.constant.core.Message.Exception.UPLOAD_ITEM_FAILED_ERROR
import com.slender.forumbackend.exception.FileNameException
import com.slender.forumbackend.exception.FileNotFoundException
import com.slender.forumbackend.exception.FileUploadFailureException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.exception.NullFileNameException
import com.slender.forumbackend.model.data.file.ImageUploadData
import com.slender.forumbackend.model.data.file.ImageUploadItemData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Locale
import java.util.UUID.randomUUID

@Component
class FileStore(
    private val client: OSSClient
) {

    private val logger = LoggerFactory.getLogger(FileStore::class.java)

    private companion object {
        const val MB = 1024 * 1024
        val validImageTypes = setOf("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif")
        val validImageExtensions = setOf("png", "jpg", "jpeg", "webp", "gif")
    }

    fun upload(
        fileName: String?,
        content: ByteArray
    ): String {
        val suffix = fileName.suffix()
        val date = now().format(ofPattern("yyyy/MM"))
        val fileId = randomUUID().toString() + suffix
        val objectName = "$date/$fileId"
        val request = newBuilder()
            .bucket(BUCKET_NAME)
            .key(objectName)
            .body(fromBytes(content))
            .build()
        return runCatching handle@{
            client.putObject(request)
            return@handle "https://$BUCKET_NAME.$ENDPOINT/$objectName"
        }.onFailure { exception ->
            logger.error(
                "OSS upload failed: bucket={}, objectName={}, exception={}",
                BUCKET_NAME,
                objectName,
                exception::class.qualifiedName,
                exception,
            )
            throw FileUploadFailureException()
        }.getOrThrow()
    }

    fun uploadImages(files: List<MultipartFile>): ImageUploadData {
        return ImageUploadData(files.map { file -> uploadImage(file) })
    }

    fun size(objectName: String): Long = runCatching {
        val request = GetObjectMetaRequest
            .newBuilder()
            .bucket(BUCKET_NAME)
            .key(objectName)
            .build()
        val result = client.getObjectMeta(request)
        result.contentLength() ?: throw FileNotFoundException()
    }.onFailure {
        throw FileNotFoundException()
    }.getOrThrow()

    private fun uploadImage(file: MultipartFile): ImageUploadItemData =
        runCatching {
            file.validateImage()
            ImageUploadItemData(
                success = true,
                url = upload(file.originalFilename, file.bytes),
            )
        }.getOrElse {
            ImageUploadItemData(
                success = false,
                message = it.uploadFailureMessage,
            )
        }

    private fun MultipartFile.validateImage() {
        if (isEmpty || size <= 0) throw InvalidRequestException(IMAGE_EMPTY_ERROR)
        if (size > 10 * MB) throw InvalidRequestException(IMAGE_TOO_LARGE_ERROR)

        val extension = originalFilename.suffix().removePrefix(".").lowercase(Locale.ROOT)
        if (extension !in validImageExtensions) throw InvalidRequestException(IMAGE_TYPE_UNSUPPORTED_ERROR)

        val normalizedContentType = contentType
            ?.substringBefore(";")
            ?.trim()
            ?.lowercase(Locale.ROOT)
            .orEmpty()
        if (normalizedContentType !in validImageTypes) throw InvalidRequestException(IMAGE_TYPE_UNSUPPORTED_ERROR)
    }

    private fun String?.suffix(): String {
        val name = this?.trim()
        if (name.isNullOrEmpty()) throw NullFileNameException()
        val dotIndex = name.lastIndexOf(".")
        if (dotIndex <= 0 || dotIndex == name.lastIndex) throw FileNameException()
        return name.substring(dotIndex)
    }

    private val Throwable.uploadFailureMessage: String
        get() = when (this) {
            is InvalidRequestException -> message
            is NullFileNameException, is FileNameException -> FILE_NAME_INVALID_ERROR
            else -> UPLOAD_ITEM_FAILED_ERROR
        }
}
