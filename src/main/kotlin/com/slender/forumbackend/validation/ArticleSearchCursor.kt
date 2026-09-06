package com.slender.forumbackend.validation

import com.slender.forumbackend.model.request.ArticleSearchRequest
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ArticleSearchCursorValidator::class])
@Target(CLASS)
@Retention(RUNTIME)
annotation class ArticleSearchCursor(
    val message: String = "游标参数错误",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class ArticleSearchCursorValidator : ConstraintValidator<ArticleSearchCursor, ArticleSearchRequest> {
    override fun isValid(value: ArticleSearchRequest?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        val firstPage = value.cursorArticleId == -1L
        val hasCursorTime = value.cursorPublishTime != null
        return (firstPage && !hasCursorTime) || (!firstPage && hasCursorTime)
    }
}
