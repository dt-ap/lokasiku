package org.lokasiku.apiservice

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lokasiku.apiservice.domain.user.User
import org.lokasiku.apiservice.domain.user.UserRepository
import org.lokasiku.apiservice.service.JwtService
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {
    final val baseUrl = "/api/v1/auth"
    val baseLoginUrl = "$baseUrl/login"
    val baseRegisterUrl = "$baseUrl/register"

    @MockBean
    lateinit var clock: Clock

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var passEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var jwtService: JwtService

    lateinit var user: User

    @BeforeEach
    fun setupEach() {
        Mockito.`when`(clock.instant()).thenReturn(Instant.parse("2021-08-06T10:00:00Z"))
        user = userRepo.save(User("test_email@mail.com", passEncoder.encode("testpass"), "Test Name"))
    }

    @Test
    fun givenCorrectCredential_whenLogin_thenSucceed() {
        mockMvc.post(baseLoginUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email": "test_email@mail.com", "password": "testpass"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.accessToken", notNullValue())
            jsonPath("$.data.user.name", `is`("Test Name"))
        }
    }

    @Test
    fun givenShortPassword_whenLogin_thenFailed() {
        mockMvc.post(baseLoginUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email": "test_email@mail.com", "password": "test"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].argument", `is`("password"))
            jsonPath("$.errors[0].description", `is`("Size must be between 8 and 64"))
        }
    }

    @Test
    fun givenNoContent_whenLogin_thenFailed() {
        mockMvc.post(baseLoginUrl) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].message", `is`("Request Body Not Exist"))
        }
    }

    @Test
    fun givenAccessToken_whenAccessingAuthenticatedRoute_thenSucceed() {
        val accessToken = jwtService.createToken("test_email@mail.com")

        mockMvc.post("$baseUrl/logout") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun givenExpiredToken_whenAccessingAuthenticatedRoute_thenFailed() {
        val accessToken = jwtService.createToken("test_email@mail.com")

        Mockito.`when`(clock.instant()).thenReturn(Instant.parse("2021-08-14T10:00:00Z"))
        mockMvc.post("$baseUrl/logout") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isForbidden() }
            jsonPath("$.errors[0].message", `is`("Access Token Expired"))
        }
    }

    @Test
    fun givenCorrectContent_whenRegister_thenSucceed() {
        mockMvc.post(baseRegisterUrl) {
            contentType = MediaType.APPLICATION_JSON
            content =
                """{"email": "test_other@mail.com", "name": "Test Name 2", "password": "testpass2", "passwordConfirm": "testpass2"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.email", `is`("test_other@mail.com"))
            jsonPath("$.data.name", `is`("Test Name 2"))
        }
    }

    @Test
    fun givenExistingEmail_whenRegister_thenFailed() {
        mockMvc.post(baseRegisterUrl) {
            contentType = MediaType.APPLICATION_JSON
            content =
                """{"email": "test_email@mail.com", "name": "Test Name", "password": "testpass2", "passwordConfirm": "testpass2"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].description", `is`("User with email: test_email@mail.com, already exist"))
        }
    }

    @Test
    fun givenDifferentPasswordConfirm_whenRegister_thenFailed() {
        mockMvc.post(baseRegisterUrl) {
            contentType = MediaType.APPLICATION_JSON
            content =
                """{"email": "test_other@mail.com", "name": "Test Name 2", "password": "testpass2", "passwordConfirm": "testpass3"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].description", `is`("Password and confirmation password do not match"))
        }
    }
}