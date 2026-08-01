package com.slender.forumbackend.validation

import com.slender.forumbackend.model.request.ArticleListRequest
import com.slender.forumbackend.model.request.ArticleListRequest.Companion.FIRST_CURSOR
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationRetention.RUNTIME

@MustBeDocumented
@Constraint(validatedBy = [ArticleListCursorValidator::class])
@Target(CLASS)
@Retention(RUNTIME)
annotation class ArticleListCursor(
    val message: String = "游标参数错误",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class ArticleListCursorValidator : ConstraintValidator<ArticleListCursor, ArticleListRequest> {
    override fun isValid(value: ArticleListRequest?, context: ConstraintValidatorContext): Boolean {
        if(value == null) return true
        val firstPage = value.cursorArticleId == FIRST_CURSOR
        val hasCursorTime = value.cursorPublishTime != null
        return (firstPage && !hasCursorTime) || (!firstPage && hasCursorTime)
    }
}
