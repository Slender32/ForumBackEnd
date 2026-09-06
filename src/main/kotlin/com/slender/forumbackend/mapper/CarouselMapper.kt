package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.carousel.Carousel
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CarouselMapper : BaseMapper<Carousel>
