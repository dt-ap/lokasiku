package org.lokasiku.apiservice.exception

import org.springframework.http.HttpStatus

/**
 * HandledRuntimeException is the superclass of RuntimeExceptions that will be handled at [org.lokasiku.apiservice.config.advice.AppResponseEntityExceptionHandler]
 */
open class AppRuntimeException(
    val msg: String = "Runtime Error",
    val desc: String = "Something's causing error at runtime.",
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
    cause: Throwable? = null
) : RuntimeException(msg, cause)