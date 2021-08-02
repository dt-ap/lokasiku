package org.lokasiku.apiservice.config.advice

import org.lokasiku.apiservice.dto.ApiErrorResponse
import org.lokasiku.apiservice.dto.ApiResponse
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class AppResponseBodyAdvice: ResponseBodyAdvice<Any?> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>) = true

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        // TODO: Throw error if Controller return Primitive Types. Should write custom error to handle this.
        // Automatically wrap Success Response with ApiResponse.
        if (returnType.containingClass.isAnnotationPresent(RestController::class.java) && returnType.method?.isAnnotationPresent(
                IgnoreResponseBodyAdvice::class.java) != true) {
            if (body !is ApiErrorResponse && body !is ApiResponse<*>) {
                val status = (response as ServletServerHttpResponse).servletResponse.status
                return ApiResponse(HttpStatus.valueOf(status), body)
            }
        }

        return body
    }
}

