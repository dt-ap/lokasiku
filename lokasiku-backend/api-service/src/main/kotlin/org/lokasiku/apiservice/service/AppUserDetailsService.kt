package org.lokasiku.apiservice.service

import org.lokasiku.apiservice.domain.user.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService(
    val userRepo: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String?): UserDetails {
        val user = userRepo.findByEmail(email) ?: throw UsernameNotFoundException(email)
        return User(email, user.passwordDigest, listOf())
    }
}