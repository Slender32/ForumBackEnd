package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.Tag
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
interface TagMapper : BaseMapper<Tag>

@Repository
class TagRepository(
    private val tagMapper: TagMapper
){
    fun findByIds(tagIds: Collection<Long>) =
        tagIds.distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { tagMapper.selectByIds(it) } ?: emptyList()
}
