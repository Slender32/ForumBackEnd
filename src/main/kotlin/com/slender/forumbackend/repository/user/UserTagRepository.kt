package com.slender.forumbackend.repository.user

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.UserTagMapper
import com.slender.forumbackend.model.entity.user.relation.UserTag
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class UserTagRepository(private val mapper: UserTagMapper) : ServiceImpl<UserTagMapper, UserTag>() {
    fun list(userId: Long): List<UserTag> =
        mapper.selectList(
            QueryWrapper<UserTag>()
                .eq("user_id", userId)
                .isNull("deleted_at")
        )

    fun find(userId: Long, tagId: Long): UserTag? =
        mapper.selectOne(
            QueryWrapper<UserTag>()
                .eq("user_id", userId)
                .eq("tag_id", tagId)
        )

    fun saveRelation(userTag: UserTag) = mapper.insert(userTag)

    fun restore(userTag: UserTag) =
        mapper.update(
            null,
            UpdateWrapper<UserTag>()
                .eq("user_id", userTag.userId)
                .eq("tag_id", userTag.tagId)
                .set("deleted_at", null),
        )

    fun delete(userId: Long, tagId: Long, now: LocalDateTime) =
        mapper.update(
            null,
            UpdateWrapper<UserTag>()
                .eq("user_id", userId)
                .eq("tag_id", tagId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )

    fun markDeletedByTag(tagId: Long, now: LocalDateTime) =
        mapper.update(
            null,
            UpdateWrapper<UserTag>()
                .eq("tag_id", tagId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )
}
