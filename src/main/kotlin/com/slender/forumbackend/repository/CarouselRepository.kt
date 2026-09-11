package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.CarouselField.ENABLED
import com.slender.forumbackend.constant.field.CarouselField.SORT_ORDER
import com.slender.forumbackend.mapper.CarouselMapper
import com.slender.forumbackend.model.entity.carousel.Carousel
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class CarouselRepository(
    private val carouselMapper: CarouselMapper
) : ServiceImpl<CarouselMapper, Carousel>(), IService<Carousel> {
    fun findEnabled(): List<Carousel> =
        carouselMapper.selectList(
            QueryWrapper<Carousel>()
                .eq(ENABLED, true)
                .isNull("deleted_at")
                .orderByAsc(SORT_ORDER)
                .last("LIMIT 8")
        )

    fun listAll(includeDeleted: Boolean = false): List<Carousel> =
        carouselMapper.selectList(
            QueryWrapper<Carousel>()
                .apply(if (!includeDeleted) "deleted_at IS NULL" else "1=1")
                .orderByAsc(SORT_ORDER)
                .orderByAsc("carousel_id")
        )

    fun findById(id: Long): Carousel? = carouselMapper.selectById(id)

    fun markDeleted(id: Long, now: LocalDateTime): Boolean =
        carouselMapper.update(
            null,
                UpdateWrapper<Carousel>()
                .eq("carousel_id", id)
                .isNull("deleted_at")
                .set("deleted_at", now)
                .set("enabled", false)
                .set("update_time", now),
        ) > 0
}
