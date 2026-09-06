package com.slender.forumbackend.service

import com.slender.forumbackend.exception.ArticleTagInvalidException
import com.slender.forumbackend.repository.article.ArticleTagRepository
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.model.data.article.ArticleTagData
import com.slender.forumbackend.model.data.article.TagDetailData
import com.slender.forumbackend.model.data.article.TagListData
import com.slender.forumbackend.model.request.TagListRequest
import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val articleTagRepository: ArticleTagRepository,
) {

    fun detail(tagId: Long): TagDetailData {
        val tag = tagRepository.findById(tagId) ?: throw ArticleTagInvalidException()
        return TagDetailData(
            tid = tag.tid,
            name = tag.name,
            color = tag.color,
            articleCount = articleTagRepository.countVisibleByTagId(tag.tid),
            description = "",
        )
    }

    fun list(request: TagListRequest): TagListData {
        val keyword = request.keyword.trim()
        val tags = if (keyword.isEmpty()) {
            tagRepository.findPopular(request.size)
        } else {
            tagRepository.findByNamePrefix(keyword, request.size)
        }
        return TagListData(items = tags.map { ArticleTagData(it.tid, it.name, it.color) })
    }
}
