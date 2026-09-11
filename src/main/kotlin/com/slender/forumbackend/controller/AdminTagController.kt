package com.slender.forumbackend.controller

import com.slender.forumbackend.facade.TagFacade
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.Response.Companion.success
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/tags")
@PreAuthorize("hasAuthority('tag:manage')")
class AdminTagController(
    private val tagFacade: TagFacade
) {
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Response<Unit> {
        tagFacade.delete(id)
        return success()
    }
}
