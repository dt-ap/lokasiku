package org.lokasiku.apiservice.config.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.lokasiku.apiservice.config.AppConfig
import org.lokasiku.apiservice.domain.user.UserRepository
import org.lokasiku.apiservice.dto.ApiError
import org.lokasiku.apiservice.dto.ApiErrorResponse
import org.lokasiku.apiservice.dto.ApiResponse
import org.lokasiku.apiservice.exception.JwtAuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.time.Clock
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class AppUsernamePasswordAuthenticationFilter(
    private val authManager: AuthenticationManager,
    private val config: AppConfig,
    private val passwordEncoder: PasswordEncoder,
    private val userRepo: UserRepository
) :
    UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        try {
            val (email, password) = jacksonObjectMapper().readValue<LoginRequest>(request.inputStream)

            email?.let {
                userRepo.findByEmail(it)?.let { user ->
                    val matched = passwordEncoder.matches(password, user.passwordDigest)
                    if (matched) return authManager.authenticate(
                        UsernamePasswordAuthenticationToken(email, password, listOf())
                    )
                }
            }

            throw JwtAuthenticationException("Bad Credential")
        } catch (ex: IOException) {
            throw JwtAuthenticationException("Bad Credential", ex)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val expirationAt = Date(Clock.systemUTC().millis() + config.jwt.expirationDuration)
        val token = JWT.create().withSubject(authResult.name)
            .withExpiresAt(expirationAt)
            .sign(Algorithm.HMAC512(config.jwt.secret))

        try {
            response.addHeader("Authorization", "Bearer $token")
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(ObjectMapper().writeValueAsString(ApiResponse.ok(mapOf("token" to token))))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        try {
            val message = failed.message ?: ""
            val description =
                if (message.isEmpty()) "" else "Authentication unsuccessful because of ${message.lowercase()}."
            val errors = listOf(ApiError(message, description))

            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpStatus.BAD_REQUEST.value()
            response.writer.write(ObjectMapper().writeValueAsString(ApiErrorResponse.badRequest(errors)))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}

data class LoginRequest(
    @JsonProperty("email") val email: String?,
    @JsonProperty("password") val password: String?
)