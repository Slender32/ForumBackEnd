package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.comment.content.Comment
import com.slender.forumbackend.model.entity.comment.content.CommentStatistics
import com.slender.forumbackend.model.entity.comment.relation.CommentLike
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
interface CommentMapper : BaseMapper<Comment>

@Mapper
interface CommentStatisticsMapper : BaseMapper<CommentStatistics>

@Mapper
interface CommentLikeMapper : BaseMapper<CommentLike>

@Repository
class CommentRepository(
    private val commentMapper: CommentMapper,
    private val commentStatisticsMapper: CommentStatisticsMapper,
    private val commentLikeMapper: CommentLikeMapper,
)
