package com.slender.forumbackend.repository.comment

import com.baomidou.mybatisplus.spring.service.IService
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl
import com.slender.forumbackend.mapper.CommentNoticeMapper
import com.slender.forumbackend.model.entity.notice.CommentNotice
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CommentNoticeRepository(
    private val commentNoticeMapper: CommentNoticeMapper,
) : ServiceImpl<CommentNoticeMapper, CommentNotice>(), IService<CommentNotice> {
    fun insert(notice: CommentNotice): Long {
        commentNoticeMapper.insert(notice)
        return notice.noticeId
    }

    fun findPageByRecipient(
        recipientId: Long,
        cursorNoticeId: Long,
        cursorCreateTime: LocalDateTime?,
        limit: Int,
    ): List<CommentNotice> = commentNoticeMapper.selectPageByRecipient(
        recipientId = recipientId,
        cursorNoticeId = cursorNoticeId,
        cursorCreateTime = cursorCreateTime,
        limit = limit,
    )

    fun countUnread(recipientId: Long): Int =
        commentNoticeMapper.countUnread(recipientId).toInt()

    fun markRead(recipientId: Long, noticeIds: List<Long>) {
        commentNoticeMapper.markRead(recipientId, noticeIds)
    }
}
