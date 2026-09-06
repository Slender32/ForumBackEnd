package com.slender.forumbackend.repository.comment

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Deleted
import com.slender.forumbackend.mapper.CommentMapper
import com.slender.forumbackend.model.entity.comment.content.Comment
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CommentContentRepository(
    private val commentMapper: CommentMapper,
) : ServiceImpl<CommentMapper, Comment>(), IService<Comment> {
    fun create(comment: Comment): Long {
        commentMapper.insert(comment)
        return comment.commentId
    }

    fun markDeleted(commentId: Long, updateTime: LocalDateTime) {
        val current = commentMapper.selectById(commentId) ?: return
        commentMapper.updateById(current.copy(status = Deleted, updateTime = updateTime))
    }
}
