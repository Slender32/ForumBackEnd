package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.Tag
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TagMapper : BaseMapper<Tag> {
    fun findPopular(@Param("limit") limit: Int): List<Tag>

    fun findByNamePrefix(@Param("pattern") pattern: String, @Param("limit") limit: Int): List<Tag>
}
