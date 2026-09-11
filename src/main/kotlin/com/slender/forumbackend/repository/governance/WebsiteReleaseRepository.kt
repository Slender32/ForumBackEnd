package com.slender.forumbackend.repository.governance

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.WebsiteReleaseMapper
import com.slender.forumbackend.model.entity.governance.WebsiteRelease
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class WebsiteReleaseRepository(
    private val mapper: WebsiteReleaseMapper,
) : ServiceImpl<WebsiteReleaseMapper, WebsiteRelease>(),
    IService<WebsiteRelease> {

    fun list(includeDeleted: Boolean): List<WebsiteRelease> =
        mapper.selectList(
            QueryWrapper<WebsiteRelease>()
                .apply(if (includeDeleted) "1=1" else "deleted_at is null")
                .orderByDesc("release_date")
                .orderByAsc("release_id")
        )

    fun find(id: Long): WebsiteRelease? = mapper.selectById(id)

    fun insert(value: WebsiteRelease) = mapper.insert(value)

    fun update(value: WebsiteRelease) = mapper.updateById(value)

    fun markDeleted(id: Long, now: LocalDateTime) =
        mapper.update(
            null,
            UpdateWrapper<WebsiteRelease>()
                .eq("release_id", id)
                .isNull("deleted_at")
                .set("deleted_at", now)
                .set("enabled", false)
                .set("update_time", now),
        )
}
