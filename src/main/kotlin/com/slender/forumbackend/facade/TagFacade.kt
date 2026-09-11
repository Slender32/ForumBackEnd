package com.slender.forumbackend.facade

import com.slender.forumbackend.model.request.TagListRequest
import com.slender.forumbackend.service.TagService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagFacade(
    private val tagService: TagService,
) {
    fun list(request: TagListRequest) = tagService.list(request)

    fun detail(tagId: Long) = tagService.detail(tagId)

    @Transactional
    fun delete(tagId: Long) = tagService.delete(tagId)
}
