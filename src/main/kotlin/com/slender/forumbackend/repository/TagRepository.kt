package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.TagMapper
import com.slender.forumbackend.model.entity.Tag
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TagRepository(
    private val tagMapper: TagMapper
) : ServiceImpl<TagMapper, Tag>(), IService<Tag> {
    fun findById(tagId: Long): Tag? = tagMapper.selectById(tagId)

    fun findByIds(tagIds: Collection<Long>) =
        tagIds.distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { tagMapper.selectByIds(it) } ?: emptyList()

    fun findOrCreate(name: String, color: Int, createTime: LocalDateTime): Tag =
        findByNameAndColor(name, color) ?: Tag(
            name = name,
            color = color,
            createTime = createTime,
        ).also { tagMapper.insert(it) }

    fun findByNameAndColor(name: String, color: Int): Tag? =
        tagMapper.selectOne(
            QueryWrapper<Tag>()
                .eq("name", name)
                .eq("color", color)
        )

    fun findPopular(limit: Int): List<Tag> = tagMapper.findPopular(limit)

    fun findByNamePrefix(prefix: String, limit: Int): List<Tag> {
        val escaped = prefix.replace("!", "!!").replace("%", "!%").replace("_", "!_")
        return tagMapper.findByNamePrefix("$escaped%", limit)
    }
}
