package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.favorite.Favorite
import org.apache.ibatis.annotations.Mapper

@Mapper
interface FavoriteMapper : BaseMapper<Favorite>
