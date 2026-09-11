package com.slender.forumbackend.repository.governance

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.SensitiveWordMapper
import com.slender.forumbackend.model.entity.governance.SensitiveWord
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class SensitiveWordRepository(
    private val mapper: SensitiveWordMapper,
) : ServiceImpl<SensitiveWordMapper, SensitiveWord>(), IService<SensitiveWord> {

    fun list(
        includeDeleted: Boolean,
        keyword: String? = null,
        enabled: Boolean? = null,
        page: Int? = null,
        size: Int? = null,
    ): List<SensitiveWord> {
        val query = filter(includeDeleted, keyword, enabled).orderByDesc("word_id")
        if (page != null && size != null) {
            val safeSize = size.coerceIn(1, 100)
            val safePage = page.coerceAtLeast(1)
            query.last("LIMIT $safeSize OFFSET ${(safePage - 1) * safeSize}")
        }
        return mapper.selectList(query)
    }

    fun count(
        includeDeleted: Boolean,
        keyword: String? = null,
        enabled: Boolean? = null,
    ): Long {
        return mapper.selectCount(filter(includeDeleted, keyword, enabled))
    }

    fun find(id: Long): SensitiveWord? = mapper.selectById(id)

    fun findActive(id: Long): SensitiveWord? = find(id)?.takeIf { it.deletedAt == null }

    fun findActiveByWord(word: String): SensitiveWord? =
        mapper.selectOne(
            QueryWrapper<SensitiveWord>()
                .apply("lower(word) = {0}", word.trim().lowercase())
                .isNull("deleted_at")
                .last("LIMIT 1"),
        )

    private fun filter(
        includeDeleted: Boolean,
        keyword: String?,
        enabled: Boolean?,
    ) =
        QueryWrapper<SensitiveWord>()
            .apply(if (includeDeleted) "1=1" else "deleted_at is null")
            .like(!keyword.isNullOrBlank(), "word", keyword?.trim())
            .eq(enabled != null, "enabled", enabled)

    fun insert(value: SensitiveWord) = mapper.insert(value)

    fun update(value: SensitiveWord) = mapper.updateById(value)

    fun markDeleted(id: Long, now: LocalDateTime) =
        mapper.update(
            null,
            UpdateWrapper<SensitiveWord>()
                .eq("word_id", id)
                .isNull("deleted_at")
                .set("deleted_at", now)
                .set("enabled", false)
                .set("update_time", now),
        )
}
