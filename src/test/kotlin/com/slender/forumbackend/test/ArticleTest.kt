package com.slender.forumbackend.test

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.slender.forumbackend.TestcontainersConfiguration
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Draft
import com.slender.forumbackend.constant.enumeration.article.ArticleStatus.Published
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility.Private
import com.slender.forumbackend.constant.enumeration.article.ArticleVisibility.Public
import com.slender.forumbackend.constant.enumeration.comment.CommentStatus
import com.slender.forumbackend.constant.enumeration.error.Error.ARTICLE_NOT_FOUND
import com.slender.forumbackend.constant.enumeration.error.Error.REQUEST_CONTENT_INVALID
import com.slender.forumbackend.constant.enumeration.user.Gender
import com.slender.forumbackend.constant.enumeration.user.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@TestConstructor(autowireMode = ALL)
@SpringBootTest
class ArticleTest(
    private val mockMvc: MockMvc,
    private val jdbcTemplate: JdbcTemplate,
) {
    @BeforeEach
    fun cleanData() {
        jdbcTemplate.update("delete from article_reactions where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from article_likes where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from article_tags where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from article_promotions where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from article_stats where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from article_contents where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from comment_stats where comment_id >= ? and comment_id < ?", COMMENT_ID_BASE, COMMENT_ID_LIMIT)
        jdbcTemplate.update("delete from comments where comment_id >= ? and comment_id < ?", COMMENT_ID_BASE, COMMENT_ID_LIMIT)
        jdbcTemplate.update("delete from articles where article_id >= ? and article_id < ?", ARTICLE_ID_BASE, ARTICLE_ID_LIMIT)
        jdbcTemplate.update("delete from article_tags where tag_id >= ? and tag_id < ?", TAG_ID_BASE, TAG_ID_LIMIT)
        jdbcTemplate.update("delete from user_tags where tag_id >= ? and tag_id < ?", TAG_ID_BASE, TAG_ID_LIMIT)
        jdbcTemplate.update("delete from tags where tid >= ? and tid < ?", TAG_ID_BASE, TAG_ID_LIMIT)
        jdbcTemplate.update("delete from user_roles where user_id >= ? and user_id < ?", USER_ID_BASE, USER_ID_LIMIT)
        jdbcTemplate.update("delete from user_stats where user_id >= ? and user_id < ?", USER_ID_BASE, USER_ID_LIMIT)
        jdbcTemplate.update("delete from user_follows where follower_id >= ? and follower_id < ?", USER_ID_BASE, USER_ID_LIMIT)
        jdbcTemplate.update("delete from user_follows where followee_id >= ? and followee_id < ?", USER_ID_BASE, USER_ID_LIMIT)
        jdbcTemplate.update("delete from users where uid >= ? and uid < ?", USER_ID_BASE, USER_ID_LIMIT)
    }

    @Test
    fun `article list returns first page aggregate slices and second page by cursor`() {
        seedArticleListScenario()

        val firstPage = document(
            mockMvc.perform(
                get("/article/list")
                    .param("cursorArticleId", FIRST_CURSOR.toString())
                    .param("size", "1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(0))
                .andReturn()
                .response
                .contentAsString
        )

        assertEquals(1, firstPage.list("$.data.items").size)
        assertTrue(firstPage.boolean("$.data.hasMore"))
        assertEquals(ARTICLE_NEW_ID, firstPage.long("$.data.nextCursor.articleId"))
        assertEquals(BASE_TIME.plusMinutes(1).toMillis(), firstPage.long("$.data.nextCursor.publishTime"))
        assertEquals(ARTICLE_NEW_ID, firstPage.long("$.data.items[0].articleId"))
        assertEquals(AUTHOR_ID, firstPage.long("$.data.items[0].author.uid"))
        assertEquals("author-avatar", firstPage.string("$.data.items[0].author.avatar"))
        assertEquals(3, firstPage.int("$.data.items[0].author.level"))
        assertEquals(100, firstPage.int("$.data.items[0].likeCount"))
        assertEquals(7, firstPage.int("$.data.items[0].commentCount"))
        assertEquals(200, firstPage.int("$.data.items[0].viewCount"))
        assertEquals(10, firstPage.list("$.data.items[0].tags").size)
        assertEquals("tag-01", firstPage.string("$.data.items[0].tags[0].name"))
        assertEquals(10, firstPage.list("$.data.items[0].promotions").size)
        assertEquals("promotion-12", firstPage.string("$.data.items[0].promotions[0].content"))
        assertEquals(PROMOTER_ID, firstPage.long("$.data.items[0].promotions[0].promoter.uid"))
        assertEquals(2, firstPage.int("$.data.items[0].promotions[0].promoter.level"))
        assertEquals(5, firstPage.list("$.data.items[0].comments").size)
        assertEquals(COMMENT_ID_BASE + 2, firstPage.long("$.data.items[0].comments[0].commentId"))
        assertEquals(1, firstPage.int("$.data.items[0].comments[0].author.level"))
        assertEquals(40, firstPage.int("$.data.items[0].comments[0].likeCount"))
        assertEquals(15, firstPage.list("$.data.items[0].reactions").size)
        assertEquals(":spark:", firstPage.string("$.data.items[0].reactions[0].emoji"))
        assertEquals(6, firstPage.int("$.data.items[0].reactions[0].count"))
        assertEquals(5, firstPage.list("$.data.items[0].reactions[0].reactors").size)
        assertEquals("reactor-avatar-06", firstPage.string("$.data.items[0].reactions[0].reactors[0]"))

        val secondPage = document(
            mockMvc.perform(
                get("/article/list")
                    .param("cursorArticleId", ARTICLE_NEW_ID.toString())
                    .param("cursorPublishTime", firstPage.long("$.data.nextCursor.publishTime").toString())
                    .param("size", "1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(0))
                .andReturn()
                .response
                .contentAsString
        )

        assertEquals(1, secondPage.list("$.data.items").size)
        assertFalse(secondPage.boolean("$.data.hasMore"))
        assertEquals(ARTICLE_OLD_ID, secondPage.long("$.data.items[0].articleId"))
        assertEquals(0, secondPage.list("$.data.items[0].tags").size)
        assertEquals(0, secondPage.list("$.data.items[0].promotions").size)
        assertEquals(0, secondPage.list("$.data.items[0].comments").size)
        assertEquals(0, secondPage.list("$.data.items[0].reactions").size)
    }

    @Test
    fun `article content returns markdown and does not increment views`() {
        seedUser(AUTHOR_ID, "author", "author-avatar", level = 3)
        seedArticle(ARTICLE_NEW_ID, AUTHOR_ID, "Markdown", Published.value, Public.value, BASE_TIME, viewCount = 321)

        mockMvc.perform(get("/article/{aid}", ARTICLE_NEW_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.content").value(MARKDOWN_CONTENT))

        val viewCount = jdbcTemplate.queryForObject<Int>(
            "select view_count from article_stats where article_id = ?",
            ARTICLE_NEW_ID,
        )
        assertEquals(321, viewCount)
    }

    @Test
    fun `article endpoints map hidden missing and missing content to article not found`() {
        seedUser(AUTHOR_ID, "author", "author-avatar", level = 3)
        seedArticle(HIDDEN_ARTICLE_ID, AUTHOR_ID, "Hidden", Draft.value, Public.value, BASE_TIME.plusMinutes(3))
        seedArticle(MISSING_CONTENT_ARTICLE_ID, AUTHOR_ID, "Missing Content", Published.value, Public.value, BASE_TIME.minusMinutes(3), withContent = false)

        mockMvc.perform(get("/article/{aid}", HIDDEN_ARTICLE_ID))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value(ARTICLE_NOT_FOUND.code))

        mockMvc.perform(get("/article/{aid}", MISSING_CONTENT_ARTICLE_ID))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value(ARTICLE_NOT_FOUND.code))

        mockMvc.perform(
            get("/article/list")
                .param("cursorArticleId", ARTICLE_NEW_ID.toString())
                .param("size", "1")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))
    }

    @Test
    fun `article list rejects invalid paging parameters`() {
        mockMvc.perform(
            get("/article/list")
                .param("cursorArticleId", "-2")
                .param("cursorPublishTime", BASE_TIME.toMillis().toString())
                .param("size", "10")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))

        mockMvc.perform(
            get("/article/list")
                .param("cursorArticleId", FIRST_CURSOR.toString())
                .param("size", "0")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))

        mockMvc.perform(
            get("/article/list")
                .param("cursorArticleId", FIRST_CURSOR.toString())
                .param("size", "11")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))

        mockMvc.perform(
            get("/article/list")
                .param("cursorArticleId", FIRST_CURSOR.toString())
                .param("cursorPublishTime", BASE_TIME.toMillis().toString())
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))

        mockMvc.perform(get("/article/list").param("size", "10"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(REQUEST_CONTENT_INVALID.code))
    }

    private fun seedArticleListScenario() {
        seedUser(AUTHOR_ID, "author", "author-avatar", level = 3)
        seedUser(SECOND_AUTHOR_ID, "second-author", "second-author-avatar", level = 4)
        seedUser(PROMOTER_ID, "promoter", "promoter-avatar", level = 2)
        seedUser(COMMENT_AUTHOR_ID, "commenter", "commenter-avatar", level = 1)
        (1..6).forEach { index ->
            seedUser(REACTION_USER_ID_BASE + index, "reactor-$index", "reactor-avatar-${index.toString().padStart(2, '0')}")
        }

        seedArticle(HIDDEN_ARTICLE_ID, AUTHOR_ID, "Hidden", Published.value, Private.value, BASE_TIME.plusMinutes(2))
        seedArticle(ARTICLE_NEW_ID, AUTHOR_ID, "Newest", Published.value, Public.value, BASE_TIME.plusMinutes(1), likeCount = 100, commentCount = 7, viewCount = 200)
        seedArticle(ARTICLE_OLD_ID, SECOND_AUTHOR_ID, "Older", Published.value, Public.value, BASE_TIME, likeCount = 5, commentCount = 0, viewCount = 10)

        (1..12).forEach { index ->
            val tagId = TAG_ID_BASE + index
            seedTag(tagId, "tag-${index.toString().padStart(2, '0')}", BASE_TIME.plusSeconds(index.toLong()))
            jdbcTemplate.update(
                "insert into article_tags(article_id, tag_id, create_time) values (?, ?, ?)",
                ARTICLE_NEW_ID,
                tagId,
                BASE_TIME.plusSeconds(index.toLong()),
            )
        }

        (1..12).forEach { index ->
            jdbcTemplate.update(
                "insert into article_promotions(promotion_id, article_id, promoter_id, content, promote_time) values (?, ?, ?, ?, ?)",
                PROMOTION_ID_BASE + index,
                ARTICLE_NEW_ID,
                PROMOTER_ID,
                "promotion-${index.toString().padStart(2, '0')}",
                BASE_TIME.plusMinutes(index.toLong()),
            )
        }

        listOf(1, 40, 5, 30, 20, 15, 10).forEachIndexed { index, likeCount ->
            seedComment(COMMENT_ID_BASE + index + 1, ARTICLE_NEW_ID, COMMENT_AUTHOR_ID, likeCount)
        }
        seedComment(COMMENT_ID_BASE + 20, ARTICLE_NEW_ID, COMMENT_AUTHOR_ID, 999, status = CommentStatus.Deleted.value)
        seedComment(COMMENT_ID_BASE + 21, ARTICLE_NEW_ID, COMMENT_AUTHOR_ID, 888, rootCommentId = COMMENT_ID_BASE + 1, parentCommentId = COMMENT_ID_BASE + 1, replyToUserId = AUTHOR_ID)

        (1..6).forEach { index ->
            seedReaction(ARTICLE_NEW_ID, REACTION_USER_ID_BASE + index, ":spark:", BASE_TIME.plusSeconds(index.toLong()))
        }
        (1..2).forEach { index ->
            seedReaction(ARTICLE_NEW_ID, REACTION_USER_ID_BASE + index, ":heart:", BASE_TIME.plusSeconds((20 + index).toLong()))
        }
        (1..14).forEach { index ->
            seedReaction(ARTICLE_NEW_ID, REACTION_USER_ID_BASE + 1, ":emoji-${index.toString().padStart(2, '0')}:", BASE_TIME.plusSeconds((40 + index).toLong()))
        }
    }

    private fun seedUser(uid: Long, name: String, avatar: String, level: Int = 0) {
        jdbcTemplate.update(
            """
            insert into users(uid, name, email, password_hash, avatar, level, gender, signature, status, create_time, update_time)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            uid,
            name,
            "article-test-$uid@example.com",
            "hash",
            avatar,
            level,
            Gender.Unknown.value,
            "",
            UserStatus.ACTIVE.value,
            BASE_TIME,
            BASE_TIME,
        )
    }

    private fun seedArticle(
        articleId: Long,
        authorId: Long,
        title: String,
        status: String,
        visibility: String,
        publishTime: LocalDateTime,
        likeCount: Int = 0,
        commentCount: Int = 0,
        viewCount: Int = 0,
        withContent: Boolean = true,
    ) {
        jdbcTemplate.update(
            """
            insert into articles(article_id, author_id, title, summary, cover, status, visibility, publish_time, revise_time, create_time, update_time)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            articleId,
            authorId,
            title,
            "summary-$articleId",
            "cover-$articleId",
            status,
            visibility,
            publishTime,
            publishTime.plusMinutes(1),
            publishTime,
            publishTime,
        )
        jdbcTemplate.update(
            "insert into article_stats(article_id, like_count, comment_count, view_count) values (?, ?, ?, ?)",
            articleId,
            likeCount,
            commentCount,
            viewCount,
        )
        if (withContent) {
            jdbcTemplate.update(
                "insert into article_contents(article_id, content) values (?, ?)",
                articleId,
                MARKDOWN_CONTENT,
            )
        }
    }

    private fun seedTag(tagId: Long, name: String, createTime: LocalDateTime) {
        jdbcTemplate.update(
            "insert into tags(tid, name, create_time) values (?, ?, ?)",
            tagId,
            name,
            createTime,
        )
    }

    private fun seedComment(
        commentId: Long,
        articleId: Long,
        authorId: Long,
        likeCount: Int,
        status: String = CommentStatus.Normal.value,
        rootCommentId: Long = 0,
        parentCommentId: Long = 0,
        replyToUserId: Long = 0,
    ) {
        jdbcTemplate.update(
            """
            insert into comments(comment_id, article_id, author_id, root_comment_id, parent_comment_id, reply_to_user_id, content, status, publish_time, create_time, update_time)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            commentId,
            articleId,
            authorId,
            rootCommentId,
            parentCommentId,
            replyToUserId,
            "comment-$commentId",
            status,
            BASE_TIME.plusSeconds(commentId - COMMENT_ID_BASE),
            BASE_TIME,
            BASE_TIME,
        )
        jdbcTemplate.update(
            "insert into comment_stats(comment_id, like_count, reply_count) values (?, ?, ?)",
            commentId,
            likeCount,
            0,
        )
    }

    private fun seedReaction(articleId: Long, userId: Long, emoji: String, createTime: LocalDateTime) {
        jdbcTemplate.update(
            "insert into article_reactions(article_id, user_id, emoji, create_time) values (?, ?, ?, ?)",
            articleId,
            userId,
            emoji,
            createTime,
        )
    }

    private fun document(body: String): DocumentContext =
        JsonPath.parse(body)

    private fun DocumentContext.long(path: String): Long =
        read<Number>(path).toLong()

    private fun DocumentContext.int(path: String): Int =
        read<Number>(path).toInt()

    private fun DocumentContext.boolean(path: String): Boolean =
        read(path)

    private fun DocumentContext.string(path: String): String =
        read(path)

    private fun DocumentContext.list(path: String): List<*> =
        read(path)

    private fun LocalDateTime.toMillis(): Long =
        toEpochSecond(ZoneOffset.ofHours(8)) * 1000

    private companion object {
        const val FIRST_CURSOR = -1L
        const val USER_ID_BASE = 920_000L
        const val USER_ID_LIMIT = 920_200L
        const val AUTHOR_ID = USER_ID_BASE + 1
        const val SECOND_AUTHOR_ID = USER_ID_BASE + 2
        const val PROMOTER_ID = USER_ID_BASE + 3
        const val COMMENT_AUTHOR_ID = USER_ID_BASE + 4
        const val REACTION_USER_ID_BASE = USER_ID_BASE + 20

        const val ARTICLE_ID_BASE = 930_000L
        const val ARTICLE_ID_LIMIT = 930_100L
        const val ARTICLE_NEW_ID = ARTICLE_ID_BASE + 1
        const val ARTICLE_OLD_ID = ARTICLE_ID_BASE + 2
        const val HIDDEN_ARTICLE_ID = ARTICLE_ID_BASE + 3
        const val MISSING_CONTENT_ARTICLE_ID = ARTICLE_ID_BASE + 4

        const val TAG_ID_BASE = 940_000L
        const val TAG_ID_LIMIT = 940_100L
        const val PROMOTION_ID_BASE = 950_000L
        const val COMMENT_ID_BASE = 960_000L
        const val COMMENT_ID_LIMIT = 960_100L

        const val MARKDOWN_CONTENT = "# Article\n\nhello **markdown**"
        val BASE_TIME: LocalDateTime = LocalDateTime.of(2026, 1, 1, 12, 0)
    }
}
