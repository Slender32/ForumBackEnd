package com.slender.forumbackend.service.user

import com.slender.forumbackend.model.data.article.ArticleTagData
import com.slender.forumbackend.model.entity.user.relation.UserTag
import com.slender.forumbackend.model.request.UserTagRequest
import com.slender.forumbackend.repository.TagRepository
import com.slender.forumbackend.repository.user.UserTagRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class UserTagService(
    private val tags: TagRepository,
    private val relations: UserTagRepository
) {
    fun list(userId: Long) =
        relations.list(userId).mapNotNull { rel ->
            tags.findById(rel.tagId)?.let { ArticleTagData(it.tid, it.name, it.color) }
        }

    fun add(userId: Long, request: UserTagRequest, operatorId: Long, authorities: Set<String>) {
        if (operatorId != userId && "relation:manage" !in authorities)
            throw AccessDeniedException("NO_PERMISSION")
        val tag = tags.findById(request.tagId) ?: throw IllegalArgumentException("标签不存在")
        val old = relations.find(userId, tag.tid)
        if (old != null) relations.restore(old)
        else relations.saveRelation(
            UserTag(
                userId,
                tag.tid,
                now(),
            )
        )
    }

    fun delete(userId: Long, tagId: Long, operatorId: Long, authorities: Set<String>) {
        if (operatorId != userId && "relation:manage" !in authorities)
            throw AccessDeniedException("NO_PERMISSION")
        relations.delete(userId, tagId, now())
    }
}
