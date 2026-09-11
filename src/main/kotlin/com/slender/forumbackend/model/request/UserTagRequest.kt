package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Positive

data class UserTagRequest(
    @field:Positive val tagId: Long
)
