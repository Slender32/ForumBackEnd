package com.slender.forumbackend.toolkit

import com.slender.forumbackend.exception.ValidationException
import jakarta.validation.Validator
import org.springframework.stereotype.Component

@Component
class Validator(
    private val validator: Validator
) {
    fun <T> validate(validationObject: T) {
        validator.validate(validationObject).apply {
            if(isNotEmpty()) throw ValidationException(first().message)
        }
    }
}
