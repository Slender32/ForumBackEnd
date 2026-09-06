package com.slender.forumbackend.component.user

import com.slender.forumbackend.configuration.OSS.Companion.BUCKET_NAME
import com.slender.forumbackend.configuration.OSS.Companion.ENDPOINT
import com.slender.forumbackend.exception.InvalidRequestException
import org.springframework.stereotype.Component
import java.net.URI

@Component
class UserProfileValidator {

    private companion object {
        const val HOST = "${BUCKET_NAME}.${ENDPOINT}"
    }

    fun validateAvatar(avatar: String) {
        val uri = runCatching { URI(avatar.trim()) }.getOrNull()
        val isAllowed = uri?.scheme == "https" &&
            uri.host == HOST && !uri.path.isNullOrBlank()
        if (!isAllowed) throw InvalidRequestException("头像必须是当前站点上传的图片资源")
    }

    fun validateSignature(signature: String) {
        //TODO 签名敏感词审核
    }
}
