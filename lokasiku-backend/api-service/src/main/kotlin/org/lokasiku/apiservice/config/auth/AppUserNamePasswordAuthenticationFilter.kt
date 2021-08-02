package org.lokasiku.apiservice.config.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.lokasiku.apiservice.config.AppConfig
import org.lokasiku.apiservice.dto.ApiResponse
import org.lokasiku.apiservice.exception.JwtAuthenticationException
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class AppUsernamePasswordAuthenticationFilter(
    private val authManager: AuthenticationManager,
    private val config: AppConfig
) :
    UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val (email, password) = jacksonObjectMapper().readValue<LoginRequest>(request.inputStream as InputStream)
            authManager.authenticate(UsernamePasswordAuthenticationToken(email, password, listOf()))
        } catch (ex: IOException) {
            throw JwtAuthenticationException(ex.message, ex)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val expirationAt = Date(System.currentTimeMillis() + config.jwt.expirationDuration)
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
}


data class LoginRequest(
    val email: String,
    val password: String
)