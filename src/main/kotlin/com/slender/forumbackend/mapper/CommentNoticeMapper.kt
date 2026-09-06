package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.model.entity.notice.CommentNotice
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

@Mapper
interface CommentNoticeMapper : BaseMapper<CommentNotice> {
    fun selectPageByRecipient(
        @Param("recipientId") recipientId: Long,
        @Param("cursorNoticeId") cursorNoticeId: Long,
        @Param("cursorCreateTime") cursorCreateTime: LocalDateTime?,
        @Param("limit") limit: Int,
    ): List<CommentNotice>

    fun countUnread(@Param("recipientId") recipientId: Long): Long

    fun markRead(
        @Param("recipientId") recipientId: Long,
        @Param("noticeIds") noticeIds: Collection<Long>,
    ): Int
}
