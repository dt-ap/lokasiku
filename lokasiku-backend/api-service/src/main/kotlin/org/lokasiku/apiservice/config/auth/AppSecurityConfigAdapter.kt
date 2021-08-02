package org.lokasiku.apiservice.config.auth

import org.lokasiku.apiservice.config.AppConfig
import org.lokasiku.apiservice.service.AppUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


@EnableWebSecurity
class AppSecurityConfigAdapter(
    val userDetailService: AppUserDetailsService,
    val config: AppConfig
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailService)?.passwordEncoder(passwordEncoder())
    }

    override fun configure(http: HttpSecurity?) {
        // Kotlin DSL
        http {
            cors { }
            csrf { disable() }
            authorizeRequests {
                authorize("/api/v1/auth/logout", authenticated)
                authorize("/api/v1/auth/*", anonymous)
                authorize(anyRequest, authenticated)
            }
            addFilterAt<UsernamePasswordAuthenticationFilter>(
                AppUsernamePasswordAuthenticationFilter(
                    authenticationManager(),
                    config,
                ).apply { setFilterProcessesUrl("/api/v1/auth/login") })
            addFilterAt<BasicAuthenticationFilter>(AppBasicAuthenticationFilter(authenticationManager()))
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
