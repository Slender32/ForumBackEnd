package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.FileFacade
import com.slender.forumbackend.constant.core.Message.Exception.IMAGE_COUNT_INVALID_ERROR
import com.slender.forumbackend.model.data.file.FileData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import com.slender.forumbackend.model.data.file.FileSizeData
import com.slender.forumbackend.model.data.file.ImageUploadData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.validation.annotation.Validated

@RestController
@RequestMapping("/files")
@Tag(name = "文件", description = "文件上传和文件信息查询API")
@Validated
class FileController(
    private val fileFacade: FileFacade,
) {

    @PostMapping
    @Operation(
        summary = "上传文件",
        description = "上传单个文件并返回可访问的文件地址",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "上传成功"),
            ApiResponse(responseCode = "400", description = "1008 文件名为空或格式错误"),
            ApiResponse(responseCode = "401", description = "1001 令牌缺失或 1002 令牌已过期"),
            ApiResponse(responseCode = "500", description = "1500 文件上传失败"),
        ]
    )
    fun uploadFile(
        @Parameter(description = "待上传文件")
        @RequestParam file: MultipartFile
    ): Response<FileData> {
        return success(fileFacade.upload(file))
    }

    @PostMapping("/image/upload")
    @Operation(
        summary = "批量上传图片",
        description = "一次上传多张图片,返回结果列表。顺序与请求严格对应,部分失败不影响整体响应。",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "请求成功(部分失败信息在items中)"),
            ApiResponse(responseCode = "400", description = "1008 文件数超限或files字段缺失"),
            ApiResponse(responseCode = "401", description = "1011 未登录"),
        ]
    )
    fun uploadImages(
        @Parameter(description = "待上传的图片文件列表,最多9张")
        @NotEmpty(message = IMAGE_COUNT_INVALID_ERROR)
        @Size(max = 9, message = IMAGE_COUNT_INVALID_ERROR)
        @RequestPart("files", required = false)
        files: List<MultipartFile>?
    ): Response<ImageUploadData> {
        return success(fileFacade.uploadImages(files.orEmpty()))
    }
    @GetMapping("/{name}/size")
    @Operation(
        summary = "获取文件大小",
        description = "根据对象名称查询文件大小，返回值单位为字节",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "查询成功"),
            ApiResponse(responseCode = "401", description = "1001 令牌缺失或 1002 令牌已过期"),
            ApiResponse(responseCode = "404", description = "1401 文件不存在"),
        ]
    )
    fun getSize(
        @Parameter(description = "文件对象名称")
        @PathVariable name: String
    ): Response<FileSizeData> {
        return success(fileFacade.size(name))
    }
}
