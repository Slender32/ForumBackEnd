package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.favorite.Favorite
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

@Mapper
interface FavoriteMapper : BaseMapper<Favorite> {
    fun selectPageByUser(
        @Param("userId") userId: Long,
        @Param("cursorFavoriteId") cursorFavoriteId: Long,
        @Param("cursorCreateTime") cursorCreateTime: LocalDateTime?,
        @Param("limit") limit: Int,
    ): List<Favorite>
}
