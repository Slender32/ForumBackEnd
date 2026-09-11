package com.slender.forumbackend.service

import com.slender.forumbackend.component.common.AuditLogger
import com.slender.forumbackend.exception.AdminResourceNotFoundException
import com.slender.forumbackend.exception.InvalidRequestException
import com.slender.forumbackend.model.entity.governance.SensitiveWord
import com.slender.forumbackend.model.request.SensitiveWordRequest
import com.slender.forumbackend.repository.governance.SensitiveWordRepository
import com.slender.forumbackend.service.SensitiveWordQueryService.Companion.CACHE_KEY
import java.time.LocalDateTime
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class SensitiveWordCommandService(
    private val repository: SensitiveWordRepository,
    private val redis: StringRedisTemplate,
    private val audit: AuditLogger,
) {
    fun add(req: SensitiveWordRequest, operator: Long): Int {
        val word = req.normalizedWord()
        val action = req.action.uppercase()
        val replacement = req.normalizedReplacement(action)
        if (repository.findActiveByWord(word) != null) {
            throw InvalidRequestException("敏感词已存在")
        }
        val entity = SensitiveWord(0, word, req.matchType.uppercase(), action, replacement, req.enabled)
        val result = repository.insert(entity)
        invalidate()
        audit.log(operator, "sensitive-word:manage", "sensitive_word", entity.id.takeIf { it > 0 }?.toString(), "CREATE")
        return result
    }

    fun update(id: Long, req: SensitiveWordRequest, operator: Long): Int {
        val current = repository.findActive(id) ?: throw AdminResourceNotFoundException("敏感词不存在")
        val word = req.normalizedWord()
        val action = req.action.uppercase()
        val replacement = req.normalizedReplacement(action)
        val duplicate = repository.findActiveByWord(word)
        if (duplicate != null && duplicate.id != id) {
            throw InvalidRequestException("敏感词已存在")
        }
        val result = repository.update(
            current.copy(
                word = word,
                matchType = req.matchType.uppercase(),
                action = action,
                replacement = replacement,
                enabled = req.enabled,
            )
        )
        invalidate()
        audit.log(operator, "sensitive-word:manage", "sensitive_word", id.toString(), "UPDATE")
        return result
    }

    fun delete(id: Long, operator: Long): Int {
        repository.findActive(id) ?: throw AdminResourceNotFoundException("敏感词不存在")
        val result = repository.markDeleted(id, LocalDateTime.now())
        if (result <= 0) throw AdminResourceNotFoundException("敏感词不存在")
        invalidate()
        audit.log(operator, "sensitive-word:manage", "sensitive_word", id.toString(), "DELETE")
        return result
    }

    fun invalidate() {
        redis.delete(CACHE_KEY)
    }

    private fun SensitiveWordRequest.normalizedWord() = word.trim()

    private fun SensitiveWordRequest.normalizedReplacement(action: String): String? {
        val value = replacement?.trim()?.takeIf { it.isNotEmpty() }
        if (action == "REPLACE" && value == null) {
            throw InvalidRequestException("替换词不能为空")
        }
        return value
    }
}
