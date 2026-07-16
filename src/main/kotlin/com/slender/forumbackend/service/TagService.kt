package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.TagMapper
import com.slender.forumbackend.mapper.TagRepository
import com.slender.forumbackend.model.entity.Tag
import org.springframework.stereotype.Service

interface TagService : IService<Tag>

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
) : TagService, ServiceImpl<TagMapper, Tag>()
