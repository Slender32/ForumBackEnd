package com.slender.forumbackend.service

import com.slender.forumbackend.constant.core.Redis.Key.HOME_CAROUSEL
import com.slender.forumbackend.repository.CarouselRepository
import com.slender.forumbackend.model.data.article.CarouselItemData
import com.slender.forumbackend.model.data.article.CarouselListData
import com.slender.forumbackend.toolkit.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class HomeService(
    private val carouselRepository: CarouselRepository,
    private val redisTemplate: StringRedisTemplate,
    private val json: Json,
) {

    fun carousel(): CarouselListData {
        redisTemplate.opsForValue().get(HOME_CAROUSEL)?.let { cached ->
            return json.parse(cached, CarouselListData::class)
        }
        val data = CarouselListData(
            items = carouselRepository.findEnabled().map { item ->
                CarouselItemData(
                    id = item.carouselId,
                    title = item.title,
                    summary = item.summary,
                    image = item.image,
                    targetType = item.targetType,
                    targetValue = item.targetValue,
                )
            }
        )
        redisTemplate.opsForValue().set(HOME_CAROUSEL, json.format(data), Duration.ofMinutes(5))
        return data
    }
}
