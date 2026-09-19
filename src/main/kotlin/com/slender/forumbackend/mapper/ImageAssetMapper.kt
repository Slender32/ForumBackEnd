package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.ImageAsset
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ImageAssetMapper : BaseMapper<ImageAsset>
