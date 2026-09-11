package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.governance.SensitiveWord
import com.slender.forumbackend.model.entity.governance.WebsiteIntroduction
import com.slender.forumbackend.model.entity.governance.WebsiteRelease
import org.apache.ibatis.annotations.Mapper

@Mapper
interface WebsiteIntroductionMapper : BaseMapper<WebsiteIntroduction>

@Mapper
interface WebsiteReleaseMapper : BaseMapper<WebsiteRelease>

@Mapper
interface SensitiveWordMapper : BaseMapper<SensitiveWord>
