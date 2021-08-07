package org.lokasiku.apiservice.domain.auth

import org.lokasiku.apiservice.domain.user.User
import org.lokasiku.apiservice.domain.user.UserRepository
import org.lokasiku.apiservice.dto.ApiResponse
import org.lokasiku.apiservice.exception.AppRuntimeException
import org.lokasiku.apiservice.service.JwtService
import org.lokasiku.apiservice.validator.PasswordConfirm
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authManager: AuthenticationManager,
    val encoder: PasswordEncoder,
    val userDetailService: UserDetailsService,
    val jwtService: JwtService,
    val userRepo: UserRepository
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse {

        val user = userRepo.findByEmail(request.email) ?: throw EmailOrPasswordIncorrectException()
        encoder.matches(request.password, user.passwordDigest) || throw EmailOrPasswordIncorrectException()

        try {
            authManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password, listOf()))
        } catch (e: BadCredentialsException) {
            throw AppRuntimeException("Bad Credentials", "Error caused by bad credentials.")
        }

        val userDetail = userDetailService.loadUserByUsername(request.email)
        val token = jwtService.createToken(userDetail.username)

        return LoginResponse(token, user)
    }

    @PostMapping("/logout")
    fun logout(): ApiResponse<Nothing> {
        return ApiResponse.ok()
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ApiResponse<User?> {
        var createdUser: User? = null
        val user = User(request.email!!, encoder.encode(request.password!!), request.name!!)

        try {
            createdUser = userRepo.save(user)
        } catch (e: DataIntegrityViolationException) {
            throw EmailAlreadyExistException(user.email)
        }

        return ApiResponse.created(createdUser)
    }
}

data class LoginRequest(
    @field:Email @field:NotBlank val email: String? = null,
    @field:Size(min = 8, max = 64) @field:NotBlank val password: String? = null
)

data class LoginResponse(
    val accessToken: String,
    val user: User
)

@PasswordConfirm
data class RegisterRequest(
    @field:Email @field:NotBlank val email: String? = null,
    @field:Size(max = 100) @field:NotBlank val name: String? = null,
    @field:Size(min = 8, max = 64) @field:NotBlank val password: String? = null,
    @field:Size(min = 8, max = 64) @field:NotBlank val passwordConfirm: String? = null
)

class EmailOrPasswordIncorrectException(
    msg: String = "Email or Password Incorrect",
    desc: String = "Either email or password is incorrect"
) :
    AppRuntimeException(msg, desc)

class EmailAlreadyExistException(
    email: String,
    msg: String = "Email Already Exist",
    desc: String = "User with email: $email, already exist"
) :
    AppRuntimeException(msg, desc)