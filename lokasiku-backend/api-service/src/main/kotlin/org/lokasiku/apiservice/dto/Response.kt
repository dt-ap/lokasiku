@file:JvmMultifileClass

package org.lokasiku.apiservice.dto

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import org.springframework.http.HttpStatus


data class ApiResponse<T>(
    var statusCode: HttpStatus,
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var data: T? = null,
) {
    companion object {
        @JvmStatic
        fun <T> ok(data: T? = null) = ApiResponse(HttpStatus.OK, data)
    }
}

data class ApiErrorResponse(
    var statusCode: HttpStatus,
    var errors: List<ApiErrorInterface> = listOf()
) {
    companion object {
        @JvmStatic
        fun badRequest(errors: List<ApiErrorInterface>) = ApiErrorResponse(HttpStatus.BAD_REQUEST, errors)
        @JvmStatic
        fun notFound(errors: List<ApiErrorInterface>) = ApiErrorResponse(HttpStatus.NOT_FOUND, errors)
    }
}

interface ApiErrorInterface {
    val message: String
    val description: String
}

data class ApiError(
    override val message: String = "",
    override val description: String = "",
) : ApiErrorInterface

data class ApiArgumentError(
    val argument: String,
    override val message: String = "",
    override val description: String = "",
) : ApiErrorInterface