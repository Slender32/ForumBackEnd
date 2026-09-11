package com.slender.forumbackend.repository.governance

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.WebsiteIntroductionMapper
import com.slender.forumbackend.model.entity.governance.WebsiteIntroduction
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class WebsiteIntroductionRepository(
    private val mapper: WebsiteIntroductionMapper,
) : ServiceImpl<WebsiteIntroductionMapper, WebsiteIntroduction>(),
    IService<WebsiteIntroduction> {

    fun list(includeDeleted: Boolean): List<WebsiteIntroduction> =
        mapper.selectList(
            QueryWrapper<WebsiteIntroduction>()
                .apply(if (includeDeleted) "1=1" else "deleted_at is null")
                .orderByAsc("sort_order")
                .orderByAsc("introduction_id")
        )

    fun find(id: Long): WebsiteIntroduction? = mapper.selectById(id)

    fun insert(value: WebsiteIntroduction) = mapper.insert(value)

    fun update(value: WebsiteIntroduction) = mapper.updateById(value)

    fun markDeleted(id: Long, now: LocalDateTime) =
        mapper.update(
            null,
            UpdateWrapper<WebsiteIntroduction>()
                .eq("introduction_id", id)
                .isNull("deleted_at")
                .set("deleted_at", now)
                .set("enabled", false)
                .set("update_time", now),
        )
}
