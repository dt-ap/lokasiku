package org.lokasiku.apiservice.advice

import org.lokasiku.apiservice.dto.ApiArgumentError
import org.lokasiku.apiservice.dto.ApiError
import org.lokasiku.apiservice.dto.ApiErrorResponse
import org.lokasiku.apiservice.exception.AppRuntimeException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
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
        val fieldErrors =
            ex.bindingResult.fieldErrors.map {
                ApiArgumentError(
                    it.field,
                    "Argument Invalid",
                    "${it.defaultMessage?.replaceFirstChar { t -> t.uppercase() }}",
                )
            }
        val globalErrors = ex.bindingResult.globalErrors.map {
            ApiError(
                "Argument(s) Invalid",
                "${it.defaultMessage?.replaceFirstChar { t -> t.uppercase() }}",
            )
        }
        val errors = fieldErrors + globalErrors
        val resp = ApiErrorResponse.badRequest(errors)
        return handleExceptionInternal(ex, resp, headers, HttpStatus.BAD_REQUEST, request)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val resp = ApiErrorResponse.badRequest(
            listOf(
                ApiError(
                    "Request Body Not Exist",
                    "Request body must exist and using JSON format."
                )
            )
        )
        return handleExceptionInternal(ex, resp, headers, HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(AppRuntimeException::class)
    fun handleAppRuntimeException(ex: AppRuntimeException, request: WebRequest): ResponseEntity<Any> {
        val status = ex.status
        val errors = listOf(ApiError(ex.msg, ex.desc))
        val resp = ApiErrorResponse(status, errors)
        return handleExceptionInternal(ex, resp, HttpHeaders(), status, request)
    }
}