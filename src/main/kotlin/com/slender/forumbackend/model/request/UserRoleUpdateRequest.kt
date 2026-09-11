package com.slender.forumbackend.model.request

import jakarta.validation.constraints.Positive

data class UserRoleUpdateRequest(
    val roleIds: Set<@Positive Long>
)
