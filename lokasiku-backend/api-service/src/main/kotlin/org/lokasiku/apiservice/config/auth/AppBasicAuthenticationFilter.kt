package org.lokasiku.apiservice.config.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.lokasiku.apiservice.config.AppConfig
import org.lokasiku.apiservice.domain.user.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AppBasicAuthenticationFilter(
    authManager: AuthenticationManager?,
    private val config: AppConfig,
    private val userRepo: UserRepository
) :
    BasicAuthenticationFilter(authManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header: String? = request.getHeader("Authorization")

        if (header?.startsWith("Bearer ") == true) {
            val decoded =
                JWT.require(Algorithm.HMAC512(config.jwt.secret)).build().verify(header.replace("Bearer ", ""))

            val subject: String? = decoded.subject
            val expiredAt = decoded.expiresAt.time

            var token = if (subject != null && expiredAt >= System.currentTimeMillis()) {
                userRepo.findByEmail(subject)?.let {
                    UsernamePasswordAuthenticationToken(subject, it.passwordDigest)
                }
            } else null
            SecurityContextHolder.getContext().authentication = token
        }

        chain.doFilter(request, response)
    }

}