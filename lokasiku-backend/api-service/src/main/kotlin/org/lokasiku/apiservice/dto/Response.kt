package org.lokasiku.apiservice.dto

import org.springframework.http.HttpStatus

data class ApiResponse<T>(
    var statusCode: HttpStatus,
    var data: T? = null,
) {
    companion object {
        @JvmStatic
        fun <T> ok(data: T? = null) = ApiResponse(HttpStatus.OK, data)

        @JvmStatic
        fun <T> created(data: T? = null) = ApiResponse(HttpStatus.CREATED, data)
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