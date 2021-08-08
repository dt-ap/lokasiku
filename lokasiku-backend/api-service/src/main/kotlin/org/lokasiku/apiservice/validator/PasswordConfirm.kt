package org.lokasiku.apiservice.validator

import org.springframework.beans.BeanWrapperImpl
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordConfirmValidator::class])
annotation class PasswordConfirm(
    val message: String = "Password and confirmation password do not match",
    val fieldName: String = "password",
    val fieldNameConfirm: String = "passwordConfirm",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PasswordConfirmValidator : ConstraintValidator<PasswordConfirm, Any> {
    private lateinit var fieldName: String
    private lateinit var fieldNameConfirm: String

    override fun initialize(constraintAnnotation: PasswordConfirm) {
        fieldName = constraintAnnotation.fieldName
        fieldNameConfirm = constraintAnnotation.fieldNameConfirm
    }

    override fun isValid(value: Any, context: ConstraintValidatorContext): Boolean {
        val fieldValue = BeanWrapperImpl(value).getPropertyValue(fieldName)
        val confirmFieldValue = BeanWrapperImpl(value).getPropertyValue(fieldNameConfirm)

        return if (fieldValue != null && confirmFieldValue != null) fieldValue == confirmFieldValue else false
    }
}