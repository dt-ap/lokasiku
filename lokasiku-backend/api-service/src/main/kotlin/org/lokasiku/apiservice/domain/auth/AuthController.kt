package org.lokasiku.apiservice.domain.auth

import org.lokasiku.apiservice.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    @PostMapping("/register")
    fun register(): List<String> {
        return listOf("Test")
    }

    @PostMapping("/logout")
    fun logout(): ApiResponse<Nothing> {
        return ApiResponse.ok(null)
    }
}

data class RegisterRequest(
    var email: String,
    var name: String,
    var passsword: String,
    var passwordConfirm: String
)