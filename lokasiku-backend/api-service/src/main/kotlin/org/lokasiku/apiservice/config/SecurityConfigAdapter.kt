package org.lokasiku.apiservice.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke


@EnableWebSecurity
class SecurityConfigAdapter : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        // Kotlin DSL
        http {
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeRequests {
                authorize("/auth", permitAll)
                authorize(anyRequest, authenticated)
            }
        }
    }
}
