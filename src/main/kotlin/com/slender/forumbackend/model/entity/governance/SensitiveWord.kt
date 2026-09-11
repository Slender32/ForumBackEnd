package com.slender.forumbackend.model.entity.governance

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.slender.forumbackend.model.data.governance.SensitiveWordData
import java.time.LocalDateTime

@TableName("sensitive_words")
data class SensitiveWord(
    @TableId("word_id")
    val id: Long,
    val word: String,
    val matchType: String,
    val action: String,
    val replacement: String?,
    val enabled: Boolean,
    val deletedAt: LocalDateTime? = null,
){
    fun toData() = SensitiveWordData(
        id = id,
        word = word,
        matchType = matchType,
        action = action,
        replacement = replacement,
        enabled = enabled,
        deletedAt = deletedAt,
    )
}
