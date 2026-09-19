package com.slender.forumbackend.repository

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.field.FavoriteField.ARTICLE_ID
import com.slender.forumbackend.constant.field.FavoriteField.CREATE_TIME
import com.slender.forumbackend.constant.field.FavoriteField.USER_ID
import com.slender.forumbackend.mapper.FavoriteMapper
import com.slender.forumbackend.model.entity.favorite.Favorite
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class FavoriteRepository(
    private val favoriteMapper: FavoriteMapper
) : ServiceImpl<FavoriteMapper, Favorite>(), IService<Favorite> {

    fun find(userId: Long, articleId: Long): Favorite? =
        favoriteMapper.selectOne(
            QueryWrapper<Favorite>()
                .eq(USER_ID, userId)
                .eq(ARTICLE_ID, articleId)
                .isNull("deleted_at")
        )

    fun insert(favorite: Favorite): Long {
        val restored =
            favoriteMapper.update(
                null,
                UpdateWrapper<Favorite>()
                    .eq(USER_ID, favorite.userId)
                    .eq(ARTICLE_ID, favorite.articleId)
                    .isNotNull("deleted_at")
                    .set("deleted_at", null)
                    .set(CREATE_TIME, favorite.createTime),
            )
        if (restored > 0)
            return favoriteMapper
                .selectOne(
                    QueryWrapper<Favorite>()
                        .eq(USER_ID, favorite.userId)
                        .eq(ARTICLE_ID, favorite.articleId)
                        .isNull("deleted_at")
                )!!
                .favoriteId
        favoriteMapper.insert(favorite)
        return favorite.favoriteId
    }

    fun delete(userId: Long, articleId: Long): Int =
        favoriteMapper.update(
            null,
            UpdateWrapper<Favorite>()
                .eq(USER_ID, userId)
                .eq(ARTICLE_ID, articleId)
                .isNull("deleted_at")
                .set("deleted_at", LocalDateTime.now()),
        )

    fun markDeletedByArticle(articleId: Long, now: LocalDateTime) =
        favoriteMapper.update(
            null,
            UpdateWrapper<Favorite>()
                .eq(ARTICLE_ID, articleId)
                .isNull("deleted_at")
                .set("deleted_at", now),
        )

    fun findPageByUser(
        userId: Long,
        cursorFavoriteId: Long,
        cursorCreateTime: LocalDateTime?,
        limit: Int,
    ): List<Favorite> = favoriteMapper.selectPageByUser(
        userId = userId,
        cursorFavoriteId = cursorFavoriteId,
        cursorCreateTime = cursorCreateTime,
        limit = limit,
    )
}
