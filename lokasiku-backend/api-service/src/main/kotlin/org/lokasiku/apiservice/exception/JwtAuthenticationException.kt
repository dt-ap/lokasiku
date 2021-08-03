package org.lokasiku.apiservice.exception

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(msg: String?, cause: Throwable? = null) : AuthenticationException(msg, cause) {
}