package com.slender.forumbackend.mapper

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus.Normal
import com.slender.forumbackend.constant.field.CommentField.ARTICLE_ID
import com.slender.forumbackend.constant.field.CommentField.PUBLISH_TIME
import com.slender.forumbackend.constant.field.CommentField.STATUS
import com.slender.forumbackend.constant.field.CommentField.ROOT_COMMENT_ID
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
) {
    private companion object {
        const val ROOT_COMMENT_NONE = 0L
    }

    fun findNormalRootByArticleIds(articleIds: Collection<Long>) =
        articleIds.distinct().takeIf { it.isNotEmpty() }?.let {
            commentMapper.selectList(
                QueryWrapper<Comment>()
                    .`in`(ARTICLE_ID, it)
                    .eq(STATUS, Normal.value)
                    .eq(ROOT_COMMENT_ID, ROOT_COMMENT_NONE)
                    .orderByDesc(PUBLISH_TIME)
            )
        } ?: emptyList()

    fun findStatisticsByIds(commentIds: Collection<Long>) =
        commentIds.distinct()
            .takeIf { it.isNotEmpty() }
            ?.let { commentStatisticsMapper.selectByIds(it) } ?: emptyList()
}
