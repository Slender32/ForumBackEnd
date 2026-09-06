package com.slender.forumbackend.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CommentReplyRequest(
    @field:NotBlank(message = "回复内容不能为空")
    @field:Size(max = 2000, message = "回复内容不能超过2000字符")
    val content: String,
)
