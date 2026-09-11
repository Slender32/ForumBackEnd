package com.slender.forumbackend.repository.comment

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Deleted
import com.slender.forumbackend.mapper.CommentMapper
import com.slender.forumbackend.model.entity.comment.content.Comment
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class CommentContentRepository(
    private val commentMapper: CommentMapper
) : ServiceImpl<CommentMapper, Comment>(), IService<Comment> {
    fun create(comment: Comment): Long {
        commentMapper.insert(comment)
        return comment.commentId
    }

    fun markDeleted(commentId: Long, updateTime: LocalDateTime) {
        val current = commentMapper.selectById(commentId) ?: return
        if (current.status != Deleted && current.deletedAt == null) {
            commentMapper.updateById(
                current.copy(status = Deleted, updateTime = updateTime, deletedAt = updateTime)
            )
        }
    }

    fun markDeletedByArticle(articleId: Long, updateTime: LocalDateTime): Int =
        commentMapper.update(
            null,
                UpdateWrapper<Comment>()
                .eq("article_id", articleId)
                .isNull("deleted_at")
                .set("status", Deleted.value)
                .set("update_time", updateTime)
                .set("deleted_at", updateTime),
        )

    fun find(commentId: Long): Comment? = commentMapper.selectById(commentId)

    fun update(commentId: Long, content: String, updateTime: LocalDateTime) {
        val current = commentMapper.selectById(commentId) ?: return
        commentMapper.updateById(current.copy(content = content, updateTime = updateTime))
    }

    fun approve(commentId: Long, content: String, updateTime: LocalDateTime): Boolean {
        val current = commentMapper.selectById(commentId) ?: return false
        if (current.deletedAt != null) return false
        return commentMapper.updateById(
            current.copy(
                content = content,
                status = CommentStatus.Normal,
                updateTime = updateTime,
                publishTime = if (current.publishTime == LocalDateTime.MIN) updateTime else current.publishTime,
            )
        ) == 1
    }
}
