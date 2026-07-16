package com.slender.forumbackend.service

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.CommentMapper
import com.slender.forumbackend.mapper.CommentRepository
import com.slender.forumbackend.model.entity.comment.content.Comment
import org.springframework.stereotype.Service

interface CommentService : IService<Comment>

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
) : CommentService, ServiceImpl<CommentMapper, Comment>()
