package org.lokasiku.apiservice.config.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.lokasiku.apiservice.domain.user.UserRepository
import org.lokasiku.apiservice.dto.ApiError
import org.lokasiku.apiservice.dto.ApiErrorResponse
import org.lokasiku.apiservice.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AppBasicAuthenticationFilter(
    authManager: AuthenticationManager?,
    private val jwtService: JwtService,
    private val userRepo: UserRepository,
) :
    BasicAuthenticationFilter(authManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header: String? = request.getHeader("Authorization")

        if (header?.startsWith("Bearer ") == true) {
            val decoded = jwtService.validateToken(header.replace("Bearer ", ""))
            val subject = decoded?.subject

            if (jwtService.isDecodedExpired(decoded)) {
                try {
                    val message = "Access Token Expired"
                    val description = "Access token already expired. Please authenticate again."
                    val errors = listOf(ApiError(message, description))
                    val status = HttpStatus.FORBIDDEN

                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = status.value()
                    response.writer.write(jacksonObjectMapper().writeValueAsString(ApiErrorResponse(status, errors)))
                } catch (e: IOException) {
                }
            } else {
                val authToken = if (subject != null) {
                    userRepo.findByEmail(subject)?.let {
                        UsernamePasswordAuthenticationToken(subject, null, listOf())
                    }
                } else null
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        chain.doFilter(request, response)
    }

}