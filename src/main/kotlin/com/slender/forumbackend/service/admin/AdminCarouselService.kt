package com.slender.forumbackend.service.admin

import com.slender.forumbackend.constant.core.Redis.Key.HOME_CAROUSEL
import com.slender.forumbackend.model.data.AdminPageData
import com.slender.forumbackend.model.entity.carousel.Carousel
import com.slender.forumbackend.model.request.AdminCarouselRequest
import com.slender.forumbackend.repository.CarouselRepository
import java.time.LocalDateTime
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class AdminCarouselService(
    private val repository: CarouselRepository,
    private val redis: StringRedisTemplate,
) {
    fun list(page: Int, size: Int, includeDeleted: Boolean): AdminPageData<Carousel> {
        val safeSize = size.coerceIn(1, 100)
        val safePage = page.coerceAtLeast(1)
        val all = repository.listAll(includeDeleted)
        return AdminPageData(
            all.drop((safePage - 1) * safeSize).take(safeSize),
            safePage,
            safeSize,
            all.size.toLong(),
        )
    }

    fun add(request: AdminCarouselRequest): Carousel {
        val now = LocalDateTime.now()
        val entity =
            Carousel(
                title = request.title.trim(),
                summary = request.summary.trim(),
                image = request.image.trim(),
                targetType = request.targetType,
                targetValue = request.targetValue.trim(),
                sortOrder = request.sortOrder,
                enabled = request.enabled,
                createTime = now,
                updateTime = now,
            )
        repository.save(entity)
        redis.delete(HOME_CAROUSEL)
        return entity
    }

    fun update(id: Long, request: AdminCarouselRequest): Carousel {
        val current = repository.findById(id) ?: throw IllegalArgumentException("轮播不存在")
        val entity =
            current.copy(
                title = request.title.trim(),
                summary = request.summary.trim(),
                image = request.image.trim(),
                targetType = request.targetType,
                targetValue = request.targetValue.trim(),
                sortOrder = request.sortOrder,
                enabled = request.enabled,
                updateTime = LocalDateTime.now(),
            )
        repository.updateById(entity)
        redis.delete(HOME_CAROUSEL)
        return entity
    }

    fun delete(id: Long) {
        repository.markDeleted(id, LocalDateTime.now())
        redis.delete(HOME_CAROUSEL)
    }
}
