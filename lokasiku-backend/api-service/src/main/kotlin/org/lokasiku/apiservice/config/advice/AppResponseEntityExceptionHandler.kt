package org.lokasiku.apiservice.config.advice

import org.lokasiku.apiservice.dto.ApiError
import org.lokasiku.apiservice.dto.ApiErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class AppResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors =
            ex.bindingResult.fieldErrors.map { ApiError("Argument Invalid", "${it.field}: ${it.defaultMessage}") }
        val response = ApiErrorResponse.badRequest(errors)
        return handleExceptionInternal(ex, response, headers, HttpStatus.BAD_REQUEST, request)
    }

}