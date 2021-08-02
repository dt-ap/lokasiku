package org.lokasiku.apiservice.config.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AppBasicAuthenticationFilter(authManager: AuthenticationManager?) :
    BasicAuthenticationFilter(authManager) {

    @Value("\${lokasiku.jwt.secret}")
    lateinit var jwtSecret: String

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header: String? = request.getHeader("Authorization")

        if (header?.startsWith("Bearer ") == true) {
            val subject: String? =
                JWT.require(Algorithm.HMAC512(jwtSecret)).build().verify(header.replace("Bearer ", "")).subject
            val authToken = if (subject != null) UsernamePasswordAuthenticationToken(subject, null) else null
            SecurityContextHolder.getContext().authentication = authToken
        }

        chain.doFilter(request, response)
    }

}