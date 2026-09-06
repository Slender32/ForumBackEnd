package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.FavoriteField.ARTICLE_ID
import com.slender.forumbackend.constant.field.FavoriteField.CREATE_TIME
import com.slender.forumbackend.constant.field.FavoriteField.FAVORITE_ID
import com.slender.forumbackend.constant.field.FavoriteField.USER_ID
import com.slender.forumbackend.mapper.FavoriteMapper
import com.slender.forumbackend.model.entity.favorite.Favorite
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class FavoriteRepository(
    private val favoriteMapper: FavoriteMapper,
) : ServiceImpl<FavoriteMapper, Favorite>(), IService<Favorite> {
    fun find(userId: Long, articleId: Long): Favorite? =
        favoriteMapper.selectOne(
            QueryWrapper<Favorite>()
                .eq(USER_ID, userId)
                .eq(ARTICLE_ID, articleId)
        )

    fun insert(favorite: Favorite): Long {
        favoriteMapper.insert(favorite)
        return favorite.favoriteId
    }

    fun delete(userId: Long, articleId: Long): Int =
        favoriteMapper.delete(
            QueryWrapper<Favorite>()
                .eq(USER_ID, userId)
                .eq(ARTICLE_ID, articleId)
        )

    fun findPageByUser(
        userId: Long,
        cursorFavoriteId: Long,
        cursorCreateTime: LocalDateTime?,
        limit: Int,
    ): List<Favorite> {
        val wrapper = QueryWrapper<Favorite>().eq(USER_ID, userId)
        if (cursorCreateTime != null) {
            wrapper.apply(
                "(create_time < {0} OR (create_time = {0} AND favorite_id < {1}))",
                cursorCreateTime,
                cursorFavoriteId,
            )
        }
        return favoriteMapper.selectList(
            wrapper
                .orderByDesc(CREATE_TIME)
                .orderByDesc(FAVORITE_ID)
                .last("LIMIT $limit")
        )
    }
}
