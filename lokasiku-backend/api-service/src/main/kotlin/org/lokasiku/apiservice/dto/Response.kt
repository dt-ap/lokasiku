@file:JvmMultifileClass
package org.lokasiku.apiservice.dto

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import org.springframework.http.HttpStatus


data class ApiResponse<T>(
    var statusCode: HttpStatus,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var data: T? = null,
) {
    companion object {
        @JvmStatic fun <T> ok(data: T? = null) = ApiResponse(HttpStatus.OK, data)
    }

}

data class ApiErrorResponse(
    var statusCode: HttpStatus,
    var errors: List<ApiError> = listOf()
) {
    companion object {
        @JvmStatic fun badRequest(errors: List<ApiError>) = ApiErrorResponse(HttpStatus.BAD_REQUEST, errors)
        @JvmStatic fun notFound(errors: List<ApiError>) = ApiErrorResponse(HttpStatus.NOT_FOUND, errors)
    }
}

data class ApiError(
    var message: String = "",
    var description: String = "",
)