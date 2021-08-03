package org.lokasiku.apiservice

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lokasiku.apiservice.domain.user.User
import org.lokasiku.apiservice.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApplicationAuthenticationTest {
    val baseUrl = "/api/v1/auth/login"

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var passEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var userRepo: UserRepository

    lateinit var mockMvc: MockMvc
    lateinit var user: User

    @BeforeEach
    fun setupEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply<DefaultMockMvcBuilder>(springSecurity()).build()
        user = userRepo.save(User("test_email@mail.com", passEncoder.encode("testpass"), "Test Name"))
    }

    @Test
    fun givenCorrectContent_whenLogin_thenSucceed() {
        mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email": "test_email@mail.com", "password": "testpass"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.token", notNullValue())
        }
    }

    @Test
    fun givenWrongContent_whenLogin_thenFailed() {
        mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email": "test_mail@mail.com", "password": "test"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].message", `is`("Bad Credential"))
        }
    }

    @Test
    fun givenEmptyContent_whenLogin_thenFailed() {
        mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].message", `is`("Bad Credential"))
        }
    }

    @Test
    fun givenIncompleteContent_whenLogin_thenFailed() {
        mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email": "test_mail@mail.com"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].message", `is`("Bad Credential"))
        }
    }
}