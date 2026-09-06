package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.CarouselField.ENABLED
import com.slender.forumbackend.constant.field.CarouselField.SORT_ORDER
import com.slender.forumbackend.mapper.CarouselMapper
import com.slender.forumbackend.model.entity.carousel.Carousel
import org.springframework.stereotype.Repository

@Repository
class CarouselRepository(
    private val carouselMapper: CarouselMapper,
) : ServiceImpl<CarouselMapper, Carousel>(), IService<Carousel> {
    fun findEnabled(): List<Carousel> =
        carouselMapper.selectList(
            QueryWrapper<Carousel>()
                .eq(ENABLED, true)
                .orderByAsc(SORT_ORDER)
                .last("LIMIT 8")
        )
}
