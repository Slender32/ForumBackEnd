package com.slender.forumbackend.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@Target(FIELD, PROPERTY, VALUE_PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = [CaptchaCodeValidator::class])
annotation class CaptchaCode(
    val message: String = "验证码格式错误",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class CaptchaCodeValidator : ConstraintValidator<CaptchaCode, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean =
        value == null || value.matches(Regex("^[1-9]\\d{5}$"))
}
