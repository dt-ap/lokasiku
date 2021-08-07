package org.lokasiku.apiservice.config.auth

import org.lokasiku.apiservice.domain.user.UserRepository
import org.lokasiku.apiservice.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Service

@EnableWebSecurity
@Service
class AppSecurityConfigAdapter(
    val userDetailService: AppUserDetailsService,
    val jwtService: JwtService,
    val userRepo: UserRepository
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailService)?.passwordEncoder(getPasswordEncoder())
    }

    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER])
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        // Kotlin DSL
        http {
            cors { }
            csrf { disable() }
            authorizeRequests {
                authorize("/api/v1/auth/logout", authenticated)
                authorize("/api/v1/auth/*", anonymous)
                authorize(anyRequest, authenticated)
            }
            addFilterAt<BasicAuthenticationFilter>(
                AppBasicAuthenticationFilter(
                    authenticationManager(),
                    jwtService,
                    userRepo
                )
            )
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
        }
    }

    @Bean
    fun getPasswordEncoder() = BCryptPasswordEncoder()
}
